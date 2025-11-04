package dao;

import domain.Attempt;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class AttemptDao {
    private final EntityManager em;
    public AttemptDao(EntityManager em) { this.em = em; }

    public void save(Attempt a) { em.persist(a); }
    public Attempt find(Long id) { return em.find(Attempt.class, id); }

    public List<Attempt> findByQuizId(Long quizId) {
        return em.createQuery(
                        "select a from Attempt a " +
                                "where a.quiz.id = :q " +
                                "order by a.startedAt desc", Attempt.class)
                .setParameter("q", quizId)
                .getResultList();
    }

    public List<Attempt> findByPlayerUsername(String username) {
        return em.createQuery(
                        "select a from Attempt a " +
                                "where a.player.username = :u " +
                                "order by a.startedAt desc", Attempt.class)
                .setParameter("u", username)
                .getResultList();
    }

    public Optional<Attempt> findLastAttempt(String username, Long quizId) {
        var list = em.createQuery(
                        "select a from Attempt a " +
                                "where a.player.username = :u and a.quiz.id = :q " +
                                "order by a.startedAt desc", Attempt.class)
                .setParameter("u", username)
                .setParameter("q", quizId)
                .setMaxResults(1)
                .getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public List<Attempt> topScoresByQuiz(Long quizId, int limit) {
        return em.createQuery(
                        "select a from Attempt a " +
                                "where a.quiz.id = :q and a.finalScore is not null " +
                                "order by a.finalScore desc, a.finishedAt asc", Attempt.class)
                .setParameter("q", quizId)
                .setMaxResults(limit)
                .getResultList();
    }

    public long countByQuiz(Long quizId) {
        return em.createQuery(
                        "select count(a) from Attempt a where a.quiz.id = :q", Long.class)
                .setParameter("q", quizId)
                .getSingleResult();
    }
}
