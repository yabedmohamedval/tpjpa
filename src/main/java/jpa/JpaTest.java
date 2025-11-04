package jpa;


import dao.AppUserDao;
import dao.AttemptDao;
import dao.QuizDao;
import domain.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class JpaTest {


	private EntityManager manager;

	public JpaTest(EntityManager manager) {
		this.manager = manager;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
			EntityManager manager = EntityManagerHelper.getEntityManager();

		JpaTest test = new JpaTest(manager);

		EntityTransaction tx = manager.getTransaction();
		tx.begin();
		try {

			// TODO create and persist entity
            test.createData();
            tx.commit();

        } catch (Exception e) {
			e.printStackTrace();
            if (tx.isActive()) tx.rollback();
		}
        test.listQuizzes();
   	    manager.close();
		EntityManagerHelper.closeEntityManagerFactory();
		System.out.println(".. done");
	}

    private void createData() {
        // users
        var userDao = new AppUserDao(manager);
        var t1 = new AppUser(); t1.setUsername("teacher1"); t1.setEmail("t1@ex.com"); t1.setRole(Role.TEACHER);
        var p1 = new AppUser(); p1.setUsername("player1");  p1.setEmail("p1@ex.com"); p1.setRole(Role.PLAYER);
        userDao.save(t1); userDao.save(p1);

        // quiz + questions + choices
        var qz = new Quiz(); qz.setTitle("Intro Java"); qz.setDescription("JPA & Java"); qz.setTimePerQuestionSec(25); qz.setOwner(t1);

        var q1 = new MCQQuestion(); q1.setLabel("JPA est…"); q1.setOrderIndex(1);
        var c1 = new Choice(); c1.setText("Spec JPA"); c1.setCorrectAnswer(true);
        var c2 = new Choice(); c2.setText("Un SGBD");  c2.setCorrectAnswer(false);
        q1.addChoice(c1); q1.addChoice(c2); qz.addQuestion(q1);

        var q2 = new TrueFalseQuestion(); q2.setLabel("Hibernate est une implémentation JPA"); q2.setOrderIndex(2); q2.setCorrect(Boolean.TRUE);
        qz.addQuestion(q2);

        var q3 = new ShortTextQuestion(); q3.setLabel("ORM Java le plus connu ?"); q3.setOrderIndex(3); q3.setExpectedRegex("(?i)hibernate");
        qz.addQuestion(q3);

        new QuizDao(manager).save(qz);     // cascade → persiste tout

        // attempt + answer
        var a1 = new Attempt(); a1.setQuiz(qz); a1.setPlayer(p1);
        var ans1 = new Answer(); ans1.setQuestion(q1); ans1.setChosen(c1); ans1.setCorrect(Boolean.TRUE);
        a1.addAnswer(ans1); a1.setFinalScore(q1.getPoints());
        new AttemptDao(manager).save(a1);
    }

    private void listQuizzes() {
        var res = manager.createQuery("select q from Quiz q", Quiz.class).getResultList();
        System.out.println("nb quiz: " + res.size());
        res.forEach(q -> System.out.println("quiz: " + q.getTitle()));
    }

}
