package jpa;

import domain.*;
import jakarta.persistence.*;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

import java.util.List;

public class N1SelectKahoot {
    public static void main(String[] args) {
        EntityManager em = EntityManagerHelper.getEntityManager();

        // (optionnel) Compteur de requêtes avec les stats Hibernate
        SessionFactory sf = em.getEntityManagerFactory().unwrap(SessionFactory.class);
        Statistics stats = sf.getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();

        long start = System.currentTimeMillis();

        // 1 requête pour récupérer les Quiz (sans leurs questions/choices)
        TypedQuery<Quiz> q = em.createQuery("select q from Quiz q", Quiz.class);
        List<Quiz> quizzes = q.getResultList();

        // Boucle qui TRIGGER le N+1 : LAZY -> 1 select par quiz pour questions,
        // et pour chaque MCQQuestion, encore des selects pour choices.
        int totalQuestions = 0;
        int totalChoices = 0;

        for (Quiz quiz : quizzes) {
            totalQuestions += quiz.getQuestions().size();   // déclenche un SELECT par quiz
            for (Question qu : quiz.getQuestions()) {
                if (qu instanceof MCQQuestion) {
                    MCQQuestion mcq = (MCQQuestion) qu;
                    totalChoices += mcq.getChoices().size();     // encore SELECT(s) pour chaque MCQ
                }
            }
        }

        long end = System.currentTimeMillis();
        System.err.println("[N+1] time(ms) = " + (end - start));
        System.err.println("[N+1] questions=" + totalQuestions + " choices=" + totalChoices);
        System.err.println("[N+1] SQL count = " + stats.getPrepareStatementCount()); // nb de requêtes

        em.close();
        EntityManagerHelper.closeEntityManagerFactory();
        System.out.println(".. N+1 done");
    }
}
