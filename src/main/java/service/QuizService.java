package service;

import jpa.EntityManagerHelper;
import jakarta.persistence.EntityManager;

import domain.*;
import web.dto.*;            // DraftQuiz / DraftQuestion / DraftChoice (wizard)
import web.api.dto.*;        // DTO REST

import java.util.List;

public class QuizService {

    /** Persistance depuis le brouillon (Servlet/JSP wizard). */
    public long createFromDraft(DraftQuiz dq, String ownerUsername) {
        if (dq == null) throw new IllegalArgumentException("draft is null");
        if (dq.getTitle()==null || dq.getTitle().isBlank()) throw new IllegalArgumentException("title required");
        if (dq.getQuestions()==null || dq.getQuestions().isEmpty()) throw new IllegalArgumentException("at least one question");

        EntityManager em = EntityManagerHelper.getEntityManager();
        var tx = em.getTransaction(); tx.begin();
        try {
            AppUser owner = em.createQuery("select u from AppUser u where u.username=:u", AppUser.class)
                    .setParameter("u", ownerUsername!=null? ownerUsername : "teacher1")
                    .setMaxResults(1).getSingleResult();

            Quiz quiz = new Quiz();
            quiz.setOwner(owner);
            quiz.setTitle(dq.getTitle());
            quiz.setDescription(dq.getDescription());
            quiz.setTimePerQuestionSec(dq.getTimePerQuestionSec()!=null? dq.getTimePerQuestionSec() : 20);

            for (DraftQuestion d : dq.getQuestions()) {
                switch (d.getType()) {
                    case MCQ -> {
                        MCQQuestion q = new MCQQuestion();
                        q.setLabel(d.getLabel());
                        q.setOrderIndex(d.getOrderIndex());
                        q.setMultiSelect(Boolean.TRUE.equals(d.getMultiSelect()));
                        if (d.getChoices()!=null) {
                            for (DraftChoice dc : d.getChoices()) {
                                Choice c = new Choice();
                                c.setText(dc.getText());
                                c.setCorrectAnswer(dc.isCorrect());
                                q.addChoice(c);
                            }
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
            em.persist(quiz);
            tx.commit();
            return quiz.getId();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    /** Persistance depuis le payload REST. */
    public long createFromDto(QuizCreateDto dto) {
        if (dto == null || dto.title==null || dto.title.isBlank()) throw new IllegalArgumentException("title required");
        if (dto.questions==null || dto.questions.isEmpty()) throw new IllegalArgumentException("at least one question");

        EntityManager em = EntityManagerHelper.getEntityManager();
        var tx = em.getTransaction(); tx.begin();
        try {
            String ownerU = (dto.ownerUsername!=null && !dto.ownerUsername.isBlank()) ? dto.ownerUsername : "teacher1";
            AppUser owner = em.createQuery("select u from AppUser u where u.username=:u", AppUser.class)
                    .setParameter("u", ownerU).setMaxResults(1).getSingleResult();

            Quiz quiz = new Quiz();
            quiz.setOwner(owner);
            quiz.setTitle(dto.title);
            quiz.setDescription(dto.description);
            quiz.setTimePerQuestionSec(dto.timePerQuestionSec!=null? dto.timePerQuestionSec : 20);

            int nextOrder = 1;
            for (QuestionDto qd : dto.questions) {
                Integer ord = (qd.orderIndex!=null)? qd.orderIndex : nextOrder++;
                switch (qd.type) {
                    case "MCQ" -> {
                        MCQQuestion q = new MCQQuestion();
                        q.setLabel(qd.label); q.setOrderIndex(ord);
                        q.setMultiSelect(Boolean.TRUE.equals(qd.multiSelect));
                        if (qd.choices!=null) {
                            for (ChoiceDto cd : qd.choices) {
                                Choice c = new Choice();
                                c.setText(cd.text);
                                c.setCorrectAnswer(Boolean.TRUE.equals(cd.correct));
                                q.addChoice(c);
                            }
                        }
                        quiz.addQuestion(q);
                    }
                    case "TF" -> {
                        TrueFalseQuestion q = new TrueFalseQuestion();
                        q.setLabel(qd.label); q.setOrderIndex(ord);
                        q.setCorrect(Boolean.TRUE.equals(qd.correct));
                        quiz.addQuestion(q);
                    }
                    case "SHORT" -> {
                        ShortTextQuestion q = new ShortTextQuestion();
                        q.setLabel(qd.label); q.setOrderIndex(ord);
                        q.setExpectedRegex(qd.expectedRegex);
                        quiz.addQuestion(q);
                    }
                    default -> throw new IllegalArgumentException("unknown question type: "+qd.type);
                }
            }
            em.persist(quiz);
            tx.commit();
            return quiz.getId();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public Quiz findWithGraph(long id) {
        EntityManager em = EntityManagerHelper.getEntityManager();
        Quiz q = em.find(Quiz.class, id);
        if (q == null) throw new jakarta.persistence.NoResultException("Quiz "+id+" not found");
        q.getQuestions().forEach(que -> {
            if (que instanceof MCQQuestion mcq) mcq.getChoices().size();
        });
        return q;
    }

    public List<Quiz> listPaged(int page, int size) {
        return EntityManagerHelper.getEntityManager()
                .createQuery("select q from Quiz q order by q.id desc", Quiz.class)
                .setFirstResult(page*size).setMaxResults(size).getResultList();
    }

    public void delete(long id) {
        EntityManager em = EntityManagerHelper.getEntityManager();
        var tx = em.getTransaction(); tx.begin();
        try {
            Quiz q = em.find(Quiz.class, id);
            if (q == null) throw new jakarta.persistence.NoResultException("Quiz "+id+" not found");
            em.remove(q);
            tx.commit();
        } catch (RuntimeException e) { if (tx.isActive()) tx.rollback(); throw e; }
    }
}
