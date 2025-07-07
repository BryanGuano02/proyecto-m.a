package servlets;

import DAO.CalificacionDAO;
import DAO.RestauranteDAO;
import DAO.UsuarioDAO;
import DTO.RestauranteDTO;
import entidades.Calificacion;
import entidades.Comensal;
import entidades.Restaurante;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import servicios.CalificacionService;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "SvDetalleRestaurante", value = "/detalleRestaurante")
public class SvDetalleRestaurante extends HttpServlet {
    private EntityManagerFactory emf;
    private RestauranteDAO restauranteDAO;
    private CalificacionDAO calificacionDAO;
    private CalificacionService calificacionService;


    @Override
    public void init() {
        emf = Persistence.createEntityManagerFactory("UFood_PU");
        restauranteDAO = new RestauranteDAO(emf);


        // Implementar CalificacionDAO igual que en SvCalificacion
        calificacionDAO = new CalificacionDAO() {
            @Override
            public boolean crear(Calificacion calificacion) {
                jakarta.persistence.EntityManager em = emf.createEntityManager();
                try {
                    em.getTransaction().begin();
                    em.persist(calificacion);
                    em.getTransaction().commit();
                    return true;
                } catch (Exception e) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    e.printStackTrace();
                    return false;
                } finally {
                    em.close();
                }
            }

            @Override
            public List<Calificacion> obtenerCalificacionesPorRestaurante(Long idRestaurante) {
                jakarta.persistence.EntityManager em = emf.createEntityManager();
                try {
                    return em.createQuery(
                                    "SELECT DISTINCT c FROM Calificacion c LEFT JOIN FETCH c.votos WHERE c.restaurante.id = :idRestaurante",
                                    Calificacion.class)
                            .setParameter("idRestaurante", idRestaurante)
                            .getResultList();
                } finally {
                    em.close();
                }
            }


            @Override
            public Double calcularPromedioCalificaciones(Long idRestaurante) {
                jakarta.persistence.EntityManager em = emf.createEntityManager();
                try {
                    Double promedio = em.createQuery(
                                    "SELECT AVG(c.puntaje) FROM Calificacion c WHERE c.restaurante.id = :idRestaurante",
                                    Double.class)
                            .setParameter("idRestaurante", idRestaurante)
                            .getSingleResult();
                    return promedio != null ? promedio : 0.0;
                } catch (Exception e) {
                    return 0.0;
                } finally {
                    em.close();
                }
            }

            @Override
            public Calificacion obtenerCalificacionPorComensalYRestaurante(Long idComensal, Long idRestaurante) {
                EntityManager em = emf.createEntityManager();
                try {
                    List<Calificacion> resultados = em.createQuery(
                                    "SELECT c FROM Calificacion c WHERE c.comensal.id = :idComensal AND c.restaurante.id = :idRestaurante",
                                    Calificacion.class)
                            .setParameter("idComensal", idComensal)
                            .setParameter("idRestaurante", idRestaurante)
                            .getResultList();
                    return resultados.isEmpty() ? null : resultados.get(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    em.close();
                }
            }
        };
        calificacionService = new CalificacionService(calificacionDAO, new UsuarioDAO(emf), restauranteDAO);
    }

//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        try {
//            // Obtener el ID del restaurante de la URL
//            String idRestauranteStr = req.getParameter("id");
//            if (idRestauranteStr == null || idRestauranteStr.isEmpty()) {
//                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de restaurante no especificado");
//                return;
//            }
//
//            Long idRestaurante = Long.parseLong(idRestauranteStr);
//            Restaurante restaurante = restauranteDAO.obtenerRestaurantePorId(idRestaurante);
//
//            if (restaurante == null) {
//                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Restaurante no encontrado");
//                return;
//            }
//
//            // Verificar si el usuario está suscrito
//            HttpSession session = req.getSession(false);
//            boolean estaSuscrito = false;
//
//            if (session != null && session.getAttribute("usuario") instanceof Comensal) {
//                Comensal comensal = (Comensal) session.getAttribute("usuario");
//                SuscripcionDAO suscripcionDAO = new SuscripcionDAO();
//                estaSuscrito = suscripcionDAO.existeSuscripcion(comensal.getId(), restaurante.getId());
//
//                // Crear DTO con información de suscripción
//                RestauranteDTO restauranteDTO = new RestauranteDTO(restaurante, estaSuscrito);
//                req.setAttribute("restaurante", restauranteDTO);
//            } else {
//                // Si no hay usuario logueado, usar objeto Restaurante directamente
//                req.setAttribute("restaurante", restaurante);
//            }
//
//            // Obtener calificaciones del restaurante
//            String orden = req.getParameter("orden");
//            List<Calificacion> calificaciones;
//
//            if ("relevancia".equalsIgnoreCase(orden)) {
//                calificaciones = calificacionService.obtenerCalificacionesOrdenadasPorVotos(idRestaurante);
//            } else {
//                calificaciones = calificacionDAO.obtenerCalificacionesPorRestaurante(idRestaurante); // orden por defecto: reciente
//            }
//
//            req.setAttribute("calificaciones", calificaciones);
//            req.setAttribute("orden", orden);
//
//
//            // Redirigir a la página de detalle
//            req.getRequestDispatcher("/detalleRestaurante.jsp").forward(req, resp);
//
//        } catch (NumberFormatException e) {
//            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de restaurante inválido");
//        } catch (Exception e) {
//            e.printStackTrace();
//            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al procesar la solicitud");
//        }
//    }
// Refactorización del método doGet para mejorar la legibilidad y manejo de errores (extract method)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Long idRestaurante = obtenerIdRestaurante(req, resp);
            if (idRestaurante == null) return;

            Restaurante restaurante = restauranteDAO.obtenerRestaurantePorId(idRestaurante);
            if (restaurante == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Restaurante no encontrado");
                return;
            }

            // Calcular y actualizar el puntaje promedio antes de enviar al JSP
            Double promedio = calificacionDAO.calcularPromedioCalificaciones(idRestaurante);
            if (promedio == null) promedio = 0.0;
            restaurante.setPuntajePromedio(promedio);

            cargarYEnviarCalificaciones(req, idRestaurante);

            req.setAttribute("restaurante", restaurante);
            req.getRequestDispatcher("/detalleRestaurante.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al procesar la solicitud");
        }
    }

    private Long obtenerIdRestaurante(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de restaurante no especificado");
            return null;
        }

        try {
            return Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de restaurante inválido");
            return null;
        }
    }


    private void cargarYEnviarCalificaciones(HttpServletRequest req, Long idRestaurante) {
        String orden = req.getParameter("orden");
        List<Calificacion> calificaciones = calificacionDAO.obtenerCalificacionesPorRestaurante(idRestaurante);

        if ("relevancia".equalsIgnoreCase(orden)) {
            calificaciones = calificacionService.ordenarCalificacionesPorVotos(calificaciones);
        }

        req.setAttribute("calificaciones", calificaciones);
        req.setAttribute("orden", orden);
    }




    @Override
    public void destroy() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
