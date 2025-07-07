package servlets;

import DAO.CalificacionDAO;
import DAO.RestauranteDAO;
import DAO.UsuarioDAO;
import entidades.Calificacion;
import entidades.Restaurante;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servicios.CalificacionService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "calificar", value = "/calificar")
public class SvCalificacion extends HttpServlet {
    private EntityManagerFactory emf;
    private RestauranteDAO restauranteDAO;
    private CalificacionDAO calificacionDAO;
    private CalificacionService calificacionService;

    @Override
    public void init() {
        emf = Persistence.createEntityManagerFactory("UFood_PU");
        UsuarioDAO usuarioDAO = new UsuarioDAO(emf);
        restauranteDAO = new RestauranteDAO(emf);  // Pasar emf en lugar de usuarioDAO
        calificacionDAO = new CalificacionDAO(emf);  // Pasar emf en lugar de usuarioDAO

        CalificacionDAO calificacionDAO = new CalificacionDAO() {
            @Override
            public boolean crear(Calificacion calificacion) {
                EntityManager em = emf.createEntityManager();
                try {
                    em.getTransaction().begin();
                    em.persist(calificacion);
                    em.getTransaction().commit();
                    return true;
                } catch (Exception e) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    return false;
                } finally {
                    em.close();
                }
            }

            @Override
            public List<Calificacion> obtenerCalificacionesPorRestaurante(Long idRestaurante) {
                EntityManager em = emf.createEntityManager();
                try {
                    return em.createQuery("SELECT c FROM Calificacion c WHERE c.restaurante.id = :idRestaurante", Calificacion.class).setParameter("idRestaurante", idRestaurante).getResultList();
                } finally {
                    em.close();
                }
            }

            @Override
            public Double calcularPromedioCalificaciones(Long idRestaurante) {
                EntityManager em = emf.createEntityManager();
                try {
                    return em.createQuery("SELECT AVG(c.puntaje) FROM Calificacion c WHERE c.restaurante.id = :idRestaurante", Double.class).setParameter("idRestaurante", idRestaurante).getSingleResult();
                } catch (Exception e) {
                    return 0.0;
                } finally {
                    em.close();
                }
            }
        };

        this.calificacionService = new CalificacionService(calificacionDAO, usuarioDAO, restauranteDAO);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long idRestaurante = obtenerIdRestaurante(req);

        if (idRestaurante == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de restaurante inválido");
            return;
        }

        Restaurante restaurante = restauranteDAO.obtenerRestaurantePorId(idRestaurante);

        if (restaurante == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Restaurante no encontrado");
            return;
        }

        List<Calificacion> calificaciones = calificacionService.obtenerCalificacionesPorRestaurante(idRestaurante);

        req.setAttribute("restaurante", restaurante);
        req.setAttribute("calificaciones", calificaciones);
        req.getRequestDispatcher("calificarRestaurante.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Map<String, Object> parametrosCalificacion = extraerParametrosCalificacion(req);
            calificacionService.calificar(parametrosCalificacion);
            try {
                Thread.sleep(2000); // 3000 milisegundos = 3 segundos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            resp.sendRedirect(req.getContextPath() + "/inicio?success=true");
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/calificar?error=" + e.getMessage());
        }
    }

    private Long obtenerIdRestaurante(HttpServletRequest req) {
        String idRestauranteString = req.getParameter("idRestaurante");
        System.out.println(idRestauranteString);
        if (idRestauranteString == null || idRestauranteString.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(idRestauranteString);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Map<String, Object> extraerParametrosCalificacion(HttpServletRequest req) {
        Map<String, Object> parametrosCalificacion = new HashMap<>();
        parametrosCalificacion.put("puntaje", Double.parseDouble(req.getParameter("puntaje")));
        parametrosCalificacion.put("comentario", req.getParameter("comentario"));
        parametrosCalificacion.put("idComensal", Long.parseLong(req.getParameter("idComensal")));
        parametrosCalificacion.put("idRestaurante", Long.parseLong(req.getParameter("idRestaurante")));

        System.out.println("Puntaje: " + Double.parseDouble(req.getParameter("puntaje")));
        System.out.println("Comentario: " + req.getParameter("comentario"));
        System.out.println("ID Comensal: " + Long.parseLong(req.getParameter("idComensal")));
        System.out.println("ID Restaurante: " + Long.parseLong(req.getParameter("idRestaurante")));
        return parametrosCalificacion;
    }

    @Override
    public void destroy() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
