package DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import entidades.Restaurante;
import jakarta.persistence.EntityTransaction;

public class RestauranteDAO {
    private final EntityManagerFactory emf;

    public RestauranteDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Restaurante obtenerRestaurantePorId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Restaurante.class, id);
        } finally {
            em.close();
        }
    }

    public List<Restaurante> obtenerTodosRestaurantes() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT r FROM Restaurante r", Restaurante.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void save(Restaurante restaurante) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (restaurante.getId() == null) {
                em.persist(restaurante);
            } else {
                em.merge(restaurante);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException("Error al guardar restaurante", e);
        } finally {
            em.close();
        }
    }
}