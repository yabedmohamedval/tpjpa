package jpa;

import domain.*;
import jakarta.persistence.*;

public class PopulateKahoot {
    public static void main(String[] args) {
        EntityManager em = EntityManagerHelper.getEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // teacher unique
        AppUser teacher = new AppUser();
        teacher.setUsername("teacher1");
        teacher.setEmail("t1@ex.com");
        teacher.setRole(Role.TEACHER);
        em.persist(teacher);

        // beaucoup de Quiz pour amplifier N+1
        final int NB_QUIZ = 500;          // augmente si tu veux
        for (int q = 0; q < NB_QUIZ; q++) {
            Quiz quiz = new Quiz();
            quiz.setOwner(teacher);
            quiz.setTitle("Quiz #" + q);
            quiz.setDescription("Demo N+1");
            quiz.setTimePerQuestionSec(20);

            // 3 questions par quiz (1 MCQ + 1 TF + 1 SHORT)
            MCQQuestion q1 = new MCQQuestion();
            q1.setLabel("JPA est…"); q1.setOrderIndex(1);
            Choice c1 = new Choice(); c1.setText("Une spec de persistance Java"); c1.setCorrectAnswer(true);
            Choice c2 = new Choice(); c2.setText("Un SGBD");                      c2.setCorrectAnswer(false);
            q1.addChoice(c1); q1.addChoice(c2);
            quiz.addQuestion(q1);

            TrueFalseQuestion q2 = new TrueFalseQuestion();
            q2.setLabel("Hibernate est une implémentation JPA"); q2.setOrderIndex(2); q2.setCorrect(Boolean.TRUE);
            quiz.addQuestion(q2);

            ShortTextQuestion q3 = new ShortTextQuestion();
            q3.setLabel("ORM Java le plus connu ?"); q3.setOrderIndex(3); q3.setExpectedRegex("(?i)hibernate");
            quiz.addQuestion(q3);

            em.persist(quiz); // cascade -> persist aussi questions+choices
            if (q % 50 == 0) em.flush(); // petites respirations
        }

        tx.commit();
        em.close();
        EntityManagerHelper.closeEntityManagerFactory();
        System.out.println(".. populate done");
    }
}
