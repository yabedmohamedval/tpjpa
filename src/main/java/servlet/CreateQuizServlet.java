package servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;
import web.dto.*;
import jpa.EntityManagerHelper;
import jakarta.persistence.EntityManager;
import domain.*;

@WebServlet(urlPatterns = {"/quizzes/new"})
public class CreateQuizServlet extends HttpServlet {

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        DraftQuiz dq = (DraftQuiz) session.getAttribute("DRAFT");
        if (dq == null) { dq = new DraftQuiz(); session.setAttribute("DRAFT", dq); }
        req.setAttribute("draft", dq);
        req.getRequestDispatcher("/WEB-INF/jsp/createQuiz.jsp").forward(req, resp);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        DraftQuiz dq = (DraftQuiz) session.getAttribute("DRAFT");
        if (dq == null) { dq = new DraftQuiz(); session.setAttribute("DRAFT", dq); }

        String action = req.getParameter("action");

        // 🔁 Toujours re-synchroniser Étape 1 si des valeurs arrivent
        String t = req.getParameter("title");
        if (t != null) dq.setTitle(t);
        String d = req.getParameter("description");
        if (d != null) dq.setDescription(d);
        String sTps = req.getParameter("tps");
        if (sTps != null) {
            try { dq.setTimePerQuestionSec(Integer.parseInt(sTps)); } catch (Exception ignored) {}
        }

        switch (action) {
            case "start" -> {
                // rien de plus, on a déjà sync ci-dessus
            }
            case "addMcq" -> {
                DraftQuestion q = new DraftQuestion();
                q.setType(DraftQType.MCQ);
                q.setLabel(req.getParameter("label"));
                Integer ord = parseInt(req.getParameter("orderIndex"));
                q.setOrderIndex(ord != null ? ord : nextOrderIndex(dq));
                q.setMultiSelect("true".equals(req.getParameter("multi")));

                DraftChoice c1 = new DraftChoice();
                c1.setText(req.getParameter("c1"));
                c1.setCorrect("true".equals(req.getParameter("c1ok")));
                DraftChoice c2 = new DraftChoice();
                c2.setText(req.getParameter("c2"));
                c2.setCorrect("true".equals(req.getParameter("c2ok")));
                if (notBlank(c1.getText())) q.getChoices().add(c1);
                if (notBlank(c2.getText())) q.getChoices().add(c2);

                dq.getQuestions().add(q);
            }
            case "addTf" -> {
                DraftQuestion q = new DraftQuestion();
                q.setType(DraftQType.TF);
                q.setLabel(req.getParameter("label"));
                Integer ord = parseInt(req.getParameter("orderIndex"));
                q.setOrderIndex(ord != null ? ord : nextOrderIndex(dq));
                q.setCorrect("true".equals(req.getParameter("correct")));
                dq.getQuestions().add(q);
            }
            case "addShort" -> {
                DraftQuestion q = new DraftQuestion();
                q.setType(DraftQType.SHORT);
                q.setLabel(req.getParameter("label"));
                Integer ord = parseInt(req.getParameter("orderIndex"));
                q.setOrderIndex(ord != null ? ord : nextOrderIndex(dq));
                q.setExpectedRegex(req.getParameter("regex"));
                dq.getQuestions().add(q);
            }
            case "finish" -> {
                // validations minimales
                if (!notBlank(dq.getTitle())) {
                    session.setAttribute("FLASH_ERR", "Veuillez renseigner le titre (Étape 1).");
                    resp.sendRedirect(req.getContextPath() + "/quizzes/new");
                    return;
                }
                if (dq.getQuestions() == null || dq.getQuestions().isEmpty()) {
                    session.setAttribute("FLASH_ERR", "Ajoutez au moins une question (Étape 2).");
                    resp.sendRedirect(req.getContextPath() + "/quizzes/new");
                    return;
                }
                persistDraft(dq, req);
                session.removeAttribute("DRAFT");
                resp.sendRedirect(req.getContextPath() + "/quizzes");
                return;
            }
            case "reset" -> {
                session.removeAttribute("DRAFT");
            }
        }
        resp.sendRedirect(req.getContextPath() + "/quizzes/new");
    }

    private void persistDraft(DraftQuiz dq, HttpServletRequest req) throws ServletException {
        EntityManager em = EntityManagerHelper.getEntityManager();
        var tx = em.getTransaction(); tx.begin();
        try {
            // owner simplifié
            AppUser owner = em.createQuery("select u from AppUser u where u.username=:u", AppUser.class)
                    .setParameter("u","teacher1").setMaxResults(1).getSingleResult();

            Quiz quiz = new Quiz();
            quiz.setOwner(owner);
            quiz.setTitle(dq.getTitle());
            quiz.setDescription(dq.getDescription());
            quiz.setTimePerQuestionSec(dq.getTimePerQuestionSec() != null ? dq.getTimePerQuestionSec() : 20);

            for (DraftQuestion d : dq.getQuestions()) {
                switch (d.getType()) {
                    case MCQ -> {
                        MCQQuestion q = new MCQQuestion();
                        q.setLabel(d.getLabel());
                        q.setOrderIndex(d.getOrderIndex());
                        q.setMultiSelect(Boolean.TRUE.equals(d.getMultiSelect()));
                        for (DraftChoice dc : d.getChoices()) {
                            Choice c = new Choice();
                            c.setText(dc.getText());
                            c.setCorrectAnswer(dc.isCorrect());
                            q.addChoice(c);
                        }
                        quiz.addQuestion(q);
                    }
                    case TF -> {
                        TrueFalseQuestion q = new TrueFalseQuestion();
                        q.setLabel(d.getLabel());
                        q.setOrderIndex(d.getOrderIndex());
                        q.setCorrect(Boolean.TRUE.equals(d.getCorrect()));
                        quiz.addQuestion(q);
                    }
                    case SHORT -> {
                        ShortTextQuestion q = new ShortTextQuestion();
                        q.setLabel(d.getLabel());
                        q.setOrderIndex(d.getOrderIndex());
                        q.setExpectedRegex(d.getExpectedRegex());
                        quiz.addQuestion(q);
                    }
                }
            }

            em.persist(quiz); // cascade vers questions/choices
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new ServletException(e);
        }
    }

    // helpers
    private static boolean notBlank(String s){ return s!=null && !s.isBlank(); }
    private static Integer parseInt(String s){ try { return s==null? null : Integer.parseInt(s); } catch(Exception e){ return null; } }
    private static int nextOrderIndex(DraftQuiz dq){
        int max = 0;
        if (dq.getQuestions()!=null) {
            for (var q : dq.getQuestions()) if (q.getOrderIndex()!=null && q.getOrderIndex()>max) max = q.getOrderIndex();
        }
        return max + 1;
    }
}
