package dao;

import domain.Quiz;
import jakarta.persistence.EntityManager;
import java.util.List;

public class QuizDao {
    private final EntityManager em;
    public QuizDao(EntityManager em){ this.em = em; }

    public void save(Quiz quiz){ em.persist(quiz); }
    public Quiz find(Long id){ return em.find(Quiz.class, id); }

    public List<Quiz> findByOwnerUsername(String username){
        return em.createQuery(
                        "select q from Quiz q where q.owner.username = :u", Quiz.class)
                .setParameter("u", username)
                .getResultList();
    }

    public List<Quiz> searchByTitle(String kw){
        return em.createNamedQuery("Quiz.searchByTitle", Quiz.class)
                .setParameter("kw", kw)
                .getResultList();
    }
}
