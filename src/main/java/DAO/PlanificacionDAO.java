package DAO;

import entidades.Planificacion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PlanificacionDAO {
    private static final Logger LOGGER = Logger.getLogger(CalificacionDAO.class.getName());
    private final EntityManagerFactory emf;

    public PlanificacionDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public PlanificacionDAO() {
        this.emf = Persistence.createEntityManagerFactory("UFood_PU");
    }

    public void save(Planificacion planificacion) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (planificacion.getId() == null) {
                em.persist(planificacion);
            } else {
                em.merge(planificacion);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive())
                tx.rollback();
            throw new RuntimeException("Error al guardar la planificación", e);
        } finally {
            em.close();
        }
    }

    public Planificacion obtenerPlanificacionPorId(Long planificacionId) {
        EntityManager em = emf.createEntityManager();
        try {
            em = emf.createEntityManager();
            return em.find(Planificacion.class, planificacionId);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }    public List<Planificacion> obtenerPlanificacionesPorId(Long idComensalPlanificador) {
        EntityManager em = emf.createEntityManager();
        try {
            return em
                    .createQuery(
                            "SELECT p FROM Planificacion p WHERE p.comensalPlanificador.id = :idComensalPlanificador",
                            Planificacion.class)
                    .setParameter("idComensalPlanificador", idComensalPlanificador)
                    .getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    public List<Planificacion> obtenerPlanificacionesPorParticipacion(Long comensalId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM Planificacion p JOIN p.comensales c WHERE c.id = :comensalId",
                    Planificacion.class)
                    .setParameter("comensalId", comensalId)
                    .getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    public List<Planificacion> obtenerTodasPlanificacionesComensal(Long comensalId) {
        List<Planificacion> resultado = new ArrayList<>();
        resultado.addAll(obtenerPlanificacionesPorId(comensalId)); // Planificaciones creadas
        resultado.addAll(obtenerPlanificacionesPorParticipacion(comensalId)); // Planificaciones donde participa
        
        // Eliminar duplicados (en caso de que el planificador también esté en la lista de comensales)
        return resultado.stream().distinct().collect(Collectors.toList());
    }
}
