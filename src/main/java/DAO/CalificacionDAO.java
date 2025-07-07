package DAO;

import entidades.Calificacion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalificacionDAO {
    private static final Logger LOGGER = Logger.getLogger(CalificacionDAO.class.getName());
    private final EntityManagerFactory emf;

    public CalificacionDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public CalificacionDAO() {
        this.emf = Persistence.createEntityManagerFactory("UFood_PU");
    }

    public Calificacion obtenerPorId(Long id) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            return em.find(Calificacion.class, id);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public boolean actualizar(Calificacion calificacion) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();
            em.merge(calificacion);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar calificación", e);
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Calificacion> obtenerCalificacionesPorRestaurante(Long idRestaurante) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            TypedQuery<Calificacion> query = em.createQuery(
                    "SELECT c FROM Calificacion c WHERE c.restaurante.id = :idRestaurante ORDER BY c.id DESC",
                    Calificacion.class);
            query.setParameter("idRestaurante", idRestaurante);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener calificaciones por restaurante", e);
            return new ArrayList<>();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Calificacion> obtenerTodasLasCalificaciones() {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            TypedQuery<Calificacion> query = em.createQuery(
                    "SELECT c FROM Calificacion c ORDER BY c.id DESC",
                    Calificacion.class);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener todas las calificaciones", e);
            return new ArrayList<>(); // Cambiado a new ArrayList<>()
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public boolean crear(Calificacion nuevaCalificacion) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();
            em.persist(nuevaCalificacion);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al crear calificación", e);
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Double calcularPromedioCalificaciones(Long idRestaurante) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            TypedQuery<Double> query = em.createQuery(
                    "SELECT AVG(c.puntaje) FROM Calificacion c WHERE c.restaurante.id = :idRestaurante",
                    Double.class);
            query.setParameter("idRestaurante", idRestaurante);
            Double promedio = query.getSingleResult();
            return promedio != null ? promedio : 0.0;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error al calcular promedio de calificaciones", e);
            return 0.0;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Calificacion obtenerCalificacionPorComensalYRestaurante(Long idComensal, Long idRestaurante) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            TypedQuery<Calificacion> query = em.createQuery(
                    "SELECT c FROM Calificacion c WHERE c.comensal.id = :idComensal AND c.restaurante.id = :idRestaurante",
                    Calificacion.class);
            query.setParameter("idComensal", idComensal);
            query.setParameter("idRestaurante", idRestaurante);

            List<Calificacion> resultados = query.getResultList();
            return resultados.isEmpty() ? null : resultados.get(0);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener calificación por comensal y restaurante", e);
            return null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}