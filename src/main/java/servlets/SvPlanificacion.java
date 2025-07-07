package servlets;

import DAO.RestauranteDAO;
import entidades.Comensal;
import entidades.Planificacion;
import entidades.Restaurante;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servicios.PlanificacionService;
import DAO.PlanificacionDAO;
import DAO.UsuarioDAO;
import DAO.VotoDAO;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "planificar", urlPatterns = { "/planificar", "/agregarComensal", "/agregarRestaurante",
        "/detallePlanificacion", "/misPlanificaciones" })
public class SvPlanificacion extends HttpServlet {
    private PlanificacionService planificacionService;
    private EntityManagerFactory emf;
    private UsuarioDAO usuarioDAO;
    private RestauranteDAO restauranteDAO;
    private VotoDAO votoDAO;

    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("UFood_PU");
        planificacionService = new PlanificacionService(new PlanificacionDAO());
        usuarioDAO = new UsuarioDAO(emf);
        votoDAO = new VotoDAO(emf);
        restauranteDAO = new RestauranteDAO(emf);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        switch (servletPath) {
            case "/detallePlanificacion":
                mostrarDetallePlanificacion(request, response);
                break;
            case "/misPlanificaciones":
                mostrarMisPlanificaciones(request, response);
                break;
            default: // /planificar
                mostrarPlanificaciones(request, response);
                break;
        }
    }

    private void mostrarPlanificaciones(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PlanificacionDAO planificacionDAO = new PlanificacionDAO();

        Long idComensalPlanificador = obtenerIdComensalPlanificador(request);

        if (idComensalPlanificador == null) {
            request.setAttribute("mensaje", "Error: No se pudo determinar el comensal planificador.");
            request.getRequestDispatcher("crearPlanificacion.jsp").forward(request, response);
            return;
        }

        List<Planificacion> planificaciones = planificacionDAO.obtenerPlanificacionesPorId(idComensalPlanificador);
        request.setAttribute("planificaciones", planificaciones);
        request.getRequestDispatcher("crearPlanificacion.jsp").forward(request, response);
    }

    private Long obtenerIdComensalPlanificador(HttpServletRequest request) {
        String idParam = request.getParameter("idComensalPlanificador");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                return Long.parseLong(idParam);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        Object usuario = request.getSession().getAttribute("usuario");
        if (usuario != null) {
            try {
                java.lang.reflect.Method getId = usuario.getClass().getMethod("getId");
                Object idObj = getId.invoke(usuario);
                if (idObj != null) {
                    return Long.parseLong(idObj.toString());
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        switch (servletPath) {
            case "/agregarComensal":
                procesarPostAgregarComensal(request, response);
                break;
            case "/agregarRestaurante":
                procesarPostAgregarRestaurante(request, response);
                break;
            default: // /planificar
                procesarPostCrearPlanificacion(request, response);
                break;
        }
    }

    private void procesarPostCrearPlanificacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nombre = request.getParameter("nombre");
        String hora = request.getParameter("hora");
        Long idComensalPlanificador = obtenerIdComensalPlanificador(request);
        String mensaje;

        if (idComensalPlanificador == null) {
            mensaje = "Error: No se pudo determinar el comensal planificador.";
            request.setAttribute("mensaje", mensaje);
            request.getRequestDispatcher("crearPlanificacion.jsp").forward(request, response);
            return;
        }

        try {
            Comensal comensal = usuarioDAO.obtenerComensalPorId(idComensalPlanificador);
            Planificacion planificacion = planificacionService.crearPlanificacion(nombre, hora, comensal);
            planificacionService.guardarPlanificacion(planificacion);
            if (planificacion != null && planificacion.getId() != null) {
                mensaje = "Planificación creada exitosamente.";
            } else {
                mensaje = "No se pudo crear la planificación.";
            }
        } catch (Exception e) {
            mensaje = "Error: " + e.getMessage();
        }

        // Mostrar las planificaciones actualizadas
        PlanificacionDAO planificacionDAO = new PlanificacionDAO();
        List<Planificacion> planificaciones = planificacionDAO.obtenerPlanificacionesPorId(idComensalPlanificador);
        request.setAttribute("planificaciones", planificaciones);
        request.setAttribute("mensaje", mensaje);
        request.getRequestDispatcher("crearPlanificacion.jsp").forward(request, response);
    }

    private void procesarPostAgregarComensal(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String planificacionIdStr = request.getParameter("planificacionId");
        String comensalIdStr = request.getParameter("comensalId");
        String mensaje;

        if (planificacionIdStr == null || planificacionIdStr.isEmpty() || comensalIdStr == null || comensalIdStr.isEmpty()) {
            mensaje = "Error: Faltan parámetros requeridos.";
            request.setAttribute("mensaje", mensaje);
            mostrarPlanificaciones(request, response);
            return;
        }

        try {
            Long planificacionId = Long.parseLong(planificacionIdStr);
            Long comensalId = Long.parseLong(comensalIdStr);

            PlanificacionDAO planificacionDAO = new PlanificacionDAO();
            Planificacion planificacion = planificacionDAO.obtenerPlanificacionPorId(planificacionId);
            Comensal comensal = usuarioDAO.obtenerComensalPorId(comensalId);

            if (planificacion == null || comensal == null) {
                mensaje = "Error: No se encontró la planificación o el comensal.";
                request.setAttribute("mensaje", mensaje);
                mostrarPlanificaciones(request, response);
                return;
            }

            try {
                planificacion.addComensal(comensal);
                planificacionDAO.save(planificacion);
                mensaje = "Comensal añadido exitosamente a la planificación.";
            } catch (IllegalArgumentException e) {
                mensaje = "Error: " + e.getMessage();
            }

        } catch (NumberFormatException e) {
            mensaje = "Error: ID inválido.";
        }

        // Redireccionar a la vista de detalle en lugar de mostrar todas las planificaciones
        response.sendRedirect(request.getContextPath() + "/detallePlanificacion?id=" + planificacionIdStr + "&mensaje=" + mensaje);
    }

    private void procesarPostAgregarRestaurante(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String planificacionIdStr = request.getParameter("planificacionId");
        String restauranteIdStr = request.getParameter("restauranteId");
        String mensaje;

        if (planificacionIdStr == null || planificacionIdStr.isEmpty() || restauranteIdStr == null || restauranteIdStr.isEmpty()) {
            mensaje = "Error: Faltan parámetros requeridos.";
            request.setAttribute("mensaje", mensaje);
            mostrarPlanificaciones(request, response);
            return;
        }

        try {
            Long planificacionId = Long.parseLong(planificacionIdStr);
            Long restauranteId = Long.parseLong(restauranteIdStr);

            PlanificacionDAO planificacionDAO = new PlanificacionDAO();
            Planificacion planificacion = planificacionDAO.obtenerPlanificacionPorId(planificacionId);
//            Restaurante restaurante = (Restaurante) usuarioDAO.findById(restauranteId);
            Restaurante restaurante = restauranteDAO.obtenerRestaurantePorId(restauranteId);

            if (planificacion == null || restaurante == null) {
                mensaje = "Error: No se encontró la planificación o el restaurante.";
                request.setAttribute("mensaje", mensaje);
                mostrarPlanificaciones(request, response);
                return;
            }

            try {
                // Add restaurant to the collection if it's not there already
                planificacion.addRestaurante(restaurante);
                mensaje = "Restaurante añadido exitosamente a la planificación.";

                planificacionDAO.save(planificacion);
            } catch (IllegalArgumentException e) {
                mensaje = "Error: " + e.getMessage();
            }

        } catch (NumberFormatException e) {
            mensaje = "Error: ID inválido.";
        }

        response.sendRedirect(request.getContextPath() + "/detallePlanificacion?id=" + planificacionIdStr + "&mensaje=" + mensaje);
    }

    private void mostrarDetallePlanificacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String planificacionIdStr = request.getParameter("id");
        String mensaje = request.getParameter("mensaje");

        if (planificacionIdStr == null || planificacionIdStr.isEmpty()) {
            mensaje = "Error: No se especificó la planificación.";
            request.setAttribute("mensaje", mensaje);
            mostrarPlanificaciones(request, response);
            return;
        }

        try {
            Long planificacionId = Long.parseLong(planificacionIdStr);
            PlanificacionDAO planificacionDAO = new PlanificacionDAO();
            Planificacion planificacion = planificacionDAO.obtenerPlanificacionPorId(planificacionId);

            if (planificacion == null) {
                mensaje = "Error: No se encontró la planificación.";
                request.setAttribute("mensaje", mensaje);
                mostrarPlanificaciones(request, response);
                return;
            }

            // Obtener la lista de restaurantes disponibles para mostrar en la pestaña de restaurantes
            List<Restaurante> restaurantes = usuarioDAO.obtenerTodosRestaurantes();

            // Obtener resultados de votación si está en curso o finalizada
            if ("En progreso".equals(planificacion.getEstadoVotacion()) ||
                "Terminada".equals(planificacion.getEstadoVotacion())) {

                Map<Restaurante, Integer> resultados = votoDAO.contarVotosPorRestaurante(planificacionId);
                request.setAttribute("resultados", resultados);

                // Obtener el voto del comensal actual
                Object usuario = request.getSession().getAttribute("usuario");
                if (usuario != null && usuario instanceof Comensal) {
                    Comensal comensalActual = (Comensal) usuario;
                    Restaurante restauranteVotado = planificacionService.obtenerVotoComensal(
                        planificacionId, comensalActual.getId());
                    request.setAttribute("restauranteVotado", restauranteVotado);
                }
            }

            // Configurar atributos para la página JSP
            if (mensaje != null) {
                request.setAttribute("mensaje", mensaje);
            }
            request.setAttribute("planificacion", planificacion);
            request.setAttribute("restaurantes", restaurantes);

            // Redirigir a la página de detalle
            request.getRequestDispatcher("detallePlanificacion.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            mensaje = "Error: ID de planificación inválido.";
            request.setAttribute("mensaje", mensaje);
            mostrarPlanificaciones(request, response);
        }
    }

    private void mostrarMisPlanificaciones(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PlanificacionDAO planificacionDAO = new PlanificacionDAO();
        
        Long idComensal = obtenerIdComensalPlanificador(request); // Reutilizamos este método que extrae el ID del comensal

        if (idComensal == null) {
            request.setAttribute("mensaje", "Error: No se pudo identificar al comensal.");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        List<Planificacion> planificaciones = planificacionDAO.obtenerTodasPlanificacionesComensal(idComensal);
        request.setAttribute("planificaciones", planificaciones);
        request.getRequestDispatcher("misPlanificaciones.jsp").forward(request, response);
    }
}
