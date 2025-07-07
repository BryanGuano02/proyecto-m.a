package DAO;

import entidades.DuenioRestaurante;
import jakarta.persistence.*;

public class DuenioRestauranteDAO {
    private final EntityManagerFactory emf;

    public DuenioRestauranteDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public DuenioRestaurante findByNombreUsuario(String nombreUsuario) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT d FROM DuenioRestaurante d LEFT JOIN FETCH d.restaurante WHERE d.nombreUsuario = :nombreUsuario",
                            DuenioRestaurante.class)
                    .setParameter("nombreUsuario", nombreUsuario)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void save(DuenioRestaurante duenio) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (duenio.getId() == null) {
                em.persist(duenio);
                if (duenio.getRestaurante() != null) {
                    em.persist(duenio.getRestaurante());
                }
            } else {
                em.merge(duenio);
                if (duenio.getRestaurante() != null) {
                    em.merge(duenio.getRestaurante());
                }
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException("Error al guardar dueño", e);
        } finally {
            em.close();
        }
    }
}
