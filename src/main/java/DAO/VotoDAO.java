package DAO;

import entidades.Comensal;
import entidades.Planificacion;
import entidades.Restaurante;
import entidades.Voto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VotoDAO {
    private final EntityManagerFactory emf;

    public VotoDAO() {
        emf = Persistence.createEntityManagerFactory("UFood_PU");
    }

    public VotoDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void save(Voto voto) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(voto);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void update(Voto voto) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(voto);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(Voto voto) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            voto = em.merge(voto);
            em.remove(voto);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Voto findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Voto.class, id);
        } finally {
            em.close();
        }
    }

    public List<Voto> obtenerVotosPorPlanificacion(Long planificacionId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Voto> query = em.createQuery(
                    "SELECT v FROM Voto v WHERE v.planificacion.id = :planificacionId",
                    Voto.class);
            query.setParameter("planificacionId", planificacionId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Voto obtenerVotoComensal(Long planificacionId, Long comensalId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Voto> query = em.createQuery(
                    "SELECT v FROM Voto v WHERE v.planificacion.id = :planificacionId AND v.comensal.id = :comensalId",
                    Voto.class);
            query.setParameter("planificacionId", planificacionId);
            query.setParameter("comensalId", comensalId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Map<Restaurante, Integer> contarVotosPorRestaurante(Long planificacionId) {
        EntityManager em = emf.createEntityManager();
        Map<Restaurante, Integer> conteoVotos = new HashMap<>();
        try {
            List<Voto> votos = obtenerVotosPorPlanificacion(planificacionId);
            for (Voto voto : votos) {
                Restaurante restaurante = voto.getRestaurante();
                conteoVotos.put(restaurante, conteoVotos.getOrDefault(restaurante, 0) + 1);
            }
            return conteoVotos;
        } finally {
            em.close();
        }
    }    public void eliminarVotosComensal(Planificacion planificacion, Comensal comensal) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Voto v WHERE v.planificacion.id = :planificacionId AND v.comensal.id = :comensalId")
                    .setParameter("planificacionId", planificacion.getId())
                    .setParameter("comensalId", comensal.getId())
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    public boolean puedeVotarComensal(Long planificacionId, Long comensalId) {
        EntityManager em = emf.createEntityManager();
        try {
            // Verificar si el comensal está en la planificación
            Long count = em.createQuery(
                    "SELECT COUNT(p) FROM Planificacion p JOIN p.comensales c WHERE p.id = :planificacionId AND c.id = :comensalId", 
                    Long.class)
                    .setParameter("planificacionId", planificacionId)
                    .setParameter("comensalId", comensalId)
                    .getSingleResult();
            
            if (count > 0) {
                return true;
            }
            
            // Verificar si el comensal es el planificador
            count = em.createQuery(
                    "SELECT COUNT(p) FROM Planificacion p WHERE p.id = :planificacionId AND p.comensalPlanificador.id = :comensalId", 
                    Long.class)
                    .setParameter("planificacionId", planificacionId)
                    .setParameter("comensalId", comensalId)
                    .getSingleResult();
                    
            return count > 0;
        } finally {
            em.close();
        }
    }
}
