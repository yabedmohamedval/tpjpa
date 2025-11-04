package dao;

import domain.AppUser;
import domain.Role;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class AppUserDao {
    private final EntityManager em;
    public AppUserDao(EntityManager em) { this.em = em; }

    public void save(AppUser u) { em.persist(u); }
    public AppUser find(Long id) { return em.find(AppUser.class, id); }

    public Optional<AppUser> findByUsername(String username) {
        var list = em.createQuery("select u from AppUser u where u.username = :u", AppUser.class)
                .setParameter("u", username)
                .setMaxResults(1)
                .getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public Optional<AppUser> findByEmail(String email) {
        var list = em.createQuery("select u from AppUser u where u.email = :e", AppUser.class)
                .setParameter("e", email)
                .setMaxResults(1)
                .getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public List<AppUser> listByRole(Role role) {
        return em.createQuery("select u from AppUser u where u.role = :r order by u.username", AppUser.class)
                .setParameter("r", role)
                .getResultList();
    }

    public boolean existsUsername(String username) {
        Long n = em.createQuery("select count(u) from AppUser u where u.username = :u", Long.class)
                .setParameter("u", username)
                .getSingleResult();
        return n > 0;
    }
}
