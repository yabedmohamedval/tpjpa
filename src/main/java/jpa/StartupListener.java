package jpa;

import domain.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

import java.util.ArrayList;
import java.util.List;

/**
 * Au démarrage de l’appli:
 * 1) Seed: create teacher1, player1, + 100 Quiz avec questions
 * 2) Mesures: N+1 vs JOIN FETCH et logs des temps + nombre de requêtes SQL
 */
@WebListener
public class StartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 1) Seed de données si base vide
        seedIfEmpty();

        // 2) Bench N+1
        runN1();

        // 3) Bench JOIN FETCH
        runJoinFetch();

        System.out.println("[StartupListener] Seed + benchmarks OK");
    }

    private void seedIfEmpty() {
        EntityManager em = EntityManagerHelper.getEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();

            Long quizCount = em.createQuery("select count(q) from Quiz q", Long.class).getSingleResult();
            if (quizCount != null && quizCount > 0) {
                tx.commit();
                System.out.println("[SEED] Base déjà peuplée (" + quizCount + " quizzes)");
                return;
            }

            // USERS
            AppUser teacher = findOrCreateUser(em, "teacher1", "t1@ex.com", "TEACHER");
            AppUser player  = findOrCreateUser(em, "player1",  "p1@ex.com", "PLAYER");

            // 100 quizzes * 3 questions (1 MCQ avec 2 choices, 1 TF, 1 SHORT)
            List<Quiz> batch = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                Quiz qz = new Quiz();
                qz.setOwner(teacher);
                qz.setTitle("Quiz #" + i);
                qz.setDescription("Auto-seed " + i);
                qz.setTimePerQuestionSec(20);

                // Q1 - MCQ
                MCQQuestion q1 = new MCQQuestion();
                q1.setLabel("Q" + i + "-1: MCQ ?");
                q1.setOrderIndex(1);
                q1.setMultiSelect(false);
                Choice c1 = new Choice(); c1.setText("Réponse A"); c1.setCorrectAnswer(true);
                Choice c2 = new Choice(); c2.setText("Réponse B"); c2.setCorrectAnswer(false);
                q1.addChoice(c1); q1.addChoice(c2);
                qz.addQuestion(q1);

                // Q2 - TF
                TrueFalseQuestion q2 = new TrueFalseQuestion();
                q2.setLabel("Q" + i + "-2: True/False ?");
                q2.setOrderIndex(2);
                q2.setCorrect(i % 2 == 0);
                qz.addQuestion(q2);

                // Q3 - SHORT
                ShortTextQuestion q3 = new ShortTextQuestion();
                q3.setLabel("Q" + i + "-3: regex 'java' ?");
                q3.setOrderIndex(3);
                q3.setExpectedRegex("(?i).*java.*");
                qz.addQuestion(q3);

                em.persist(qz); // cascade → questions/choices
                batch.add(qz);

                // flush par batch pour éviter trop de mémoire
                if (i % 25 == 0) {
                    em.flush();
                    em.clear();
                }
            }

            tx.commit();
            System.out.println("[SEED] Créés: users(teacher1,player1) + 100 quizzes x 3 questions");
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }

    private AppUser findOrCreateUser(EntityManager em, String username, String email, String role) {
        try {
            return em.createNamedQuery("AppUser.findByUsername", AppUser.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException nre) {
            AppUser u = new AppUser();
            u.setUsername(username);
            u.setEmail(email);
            u.setRole(Role.valueOf(role)); // Enum string TEACHER/PLAYER
            em.persist(u);
            return u;
        }
    }

    private void runN1() {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            SessionFactory sf = em.getEntityManagerFactory().unwrap(SessionFactory.class);
            Statistics stats = sf.getStatistics();
            stats.setStatisticsEnabled(true);
            stats.clear();

            long start = System.currentTimeMillis();

            // Charge seulement les Quiz (pas leurs associations)
            var q = em.createQuery("select q from Quiz q", Quiz.class);
            var quizzes = q.getResultList();

            int totalQuestions = 0;
            int totalChoices = 0;

            // Itération qui déclenche N+1 (LAZY)
            for (Quiz quiz : quizzes) {
                totalQuestions += quiz.getQuestions().size();
                for (Question qu : quiz.getQuestions()) {
                    if (qu instanceof MCQQuestion mcq) {
                        totalChoices += mcq.getChoices().size();
                    }
                }
            }

            long end = System.currentTimeMillis();
            System.err.println("[N+1] time(ms) = " + (end - start));
            System.err.println("[N+1] questions=" + totalQuestions + " choices=" + totalChoices);
            System.err.println("[N+1] SQL count = " + stats.getPrepareStatementCount());
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }

    private void runJoinFetch() {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            SessionFactory sf = em.getEntityManagerFactory().unwrap(SessionFactory.class);
            Statistics stats = sf.getStatistics();
            stats.setStatisticsEnabled(true);
            stats.clear();

            long start = System.currentTimeMillis();

            // JOIN FETCH questions + choices seulement pour MCQ
            var q = em.createQuery(
                    "select distinct qz " +
                            "from Quiz qz " +
                            "left join fetch qz.questions qs " +
                            "left join fetch treat(qs as MCQQuestion).choices ch",
                    Quiz.class);

            var quizzes = q.getResultList();

            int totalQuestions = 0;
            int totalChoices = 0;
            for (Quiz quiz : quizzes) {
                totalQuestions += quiz.getQuestions().size();
                for (Question qu : quiz.getQuestions()) {
                    if (qu instanceof MCQQuestion mcq) {
                        totalChoices += mcq.getChoices().size();
                    }
                }
            }

            long end = System.currentTimeMillis();
            System.err.println("[FETCH] time(ms) = " + (end - start));
            System.err.println("[FETCH] questions=" + totalQuestions + " choices=" + totalChoices);
            System.err.println("[FETCH] SQL count = " + stats.getPrepareStatementCount());
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }
}
