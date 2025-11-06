package servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Comparator;

import jakarta.persistence.EntityManager;
import jpa.EntityManagerHelper;
import domain.*;
import jakarta.persistence.NoResultException;

@WebServlet(urlPatterns = "/quizzes/view", name = "quizDetail")
public class QuizDetailServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Long id;
        try { id = Long.valueOf(req.getParameter("id")); }
        catch (Exception e) { resp.sendRedirect(req.getContextPath()+"/quizzes"); return; }

        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            // 1) Quiz + owner + toutes les questions (pas de TREAT ici)
            Quiz quiz = em.createQuery(
                            "select distinct q from Quiz q " +
                                    " join fetch q.owner o " +
                                    " left join fetch q.questions qu " +      // TF, SHORT, MCQ → tout est là
                                    " where q.id = :id", Quiz.class)
                    .setParameter("id", id)
                    .getSingleResult();

            // 2) Pré-charger les choices des MCQ en un seul select
            em.createQuery(
                            "select distinct m from MCQQuestion m " +
                                    " left join fetch m.choices c " +
                                    " where m.quiz.id = :id", MCQQuestion.class)
                    .setParameter("id", id)
                    .getResultList(); // on ne s’en sert pas, mais le PC est hydraté

            // Tri en mémoire
            quiz.getQuestions().sort(Comparator.comparing(Question::getOrderIndex));

            req.setAttribute("quiz", quiz);
            req.getRequestDispatcher("/WEB-INF/jsp/quizDetail.jsp").forward(req, resp);
        } catch (NoResultException nre) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Quiz introuvable");
        }
    }
}
