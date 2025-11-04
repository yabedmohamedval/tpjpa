package jpa;


import domain.*;
import jakarta.persistence.*;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

import java.util.List;

public class JoinFetchKahoot {
    public static void main(String[] args) {
        EntityManager em = EntityManagerHelper.getEntityManager();

        SessionFactory sf = em.getEntityManagerFactory().unwrap(SessionFactory.class);
        Statistics stats = sf.getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();

        long start = System.currentTimeMillis();

        // 1) FETCH questions
        // 2) FETCH choices uniquement pour les MCQ via TREAT
        TypedQuery<Quiz> q = em.createQuery(
                "select distinct qz " +
                        "from Quiz qz " +
                        "left join fetch qz.questions qs " +
                        "left join fetch treat(qs as MCQQuestion).choices ch",
                Quiz.class);

        List<Quiz> quizzes = q.getResultList();

        // Accéder ne déclenche plus de SELECT additionnels
        int totalQuestions = 0;
        int totalChoices = 0;
        for (Quiz quiz : quizzes) {
            totalQuestions += quiz.getQuestions().size();
            for (Question qu : quiz.getQuestions()) {
                if (qu instanceof MCQQuestion) {
                    MCQQuestion mcq = (MCQQuestion) qu;
                    totalChoices += mcq.getChoices().size();
                }
            }
        }

        long end = System.currentTimeMillis();
        System.err.println("[FETCH] time(ms) = " + (end - start));
        System.err.println("[FETCH] questions=" + totalQuestions + " choices=" + totalChoices);
        System.err.println("[FETCH] SQL count = " + stats.getPrepareStatementCount()); // nb de requêtes

        em.close();
        EntityManagerHelper.closeEntityManagerFactory();
        System.out.println(".. fetch done");
    }
}
