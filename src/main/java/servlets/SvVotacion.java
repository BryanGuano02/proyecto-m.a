package servlets;

import DAO.PlanificacionDAO;
import DAO.UsuarioDAO;
import DAO.VotoDAO;
import entidades.Comensal;
import entidades.Planificacion;
import entidades.Restaurante;
import entidades.Voto;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servicios.PlanificacionService;

import java.io.IOException;
import java.util.Map;

@WebServlet(name = "votacion", urlPatterns = {"/iniciarVotacion", "/votar", "/terminarVotacion", "/resultadosVotacion"})
public class SvVotacion extends HttpServlet {
    private EntityManagerFactory emf;
    private PlanificacionService planificacionService;
    private UsuarioDAO usuarioDAO;
    private VotoDAO votoDAO;

    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("UFood_PU");
        planificacionService = new PlanificacionService(new PlanificacionDAO());
        usuarioDAO = new UsuarioDAO(emf);
        votoDAO = new VotoDAO(emf);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        switch (servletPath) {
            case "/resultadosVotacion":
                mostrarResultadosVotacion(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/detallePlanificacion");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();
        String mensaje = "";

        switch (servletPath) {
            case "/iniciarVotacion":
                procesarIniciarVotacion(request, response);
                break;
            case "/votar":
                procesarVotar(request, response);
                break;
            case "/terminarVotacion":
                procesarTerminarVotacion(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/detallePlanificacion");
                break;
        }
    }

    private void procesarIniciarVotacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String planificacionIdStr = request.getParameter("planificacionId");
        String mensaje;

        if (planificacionIdStr == null || planificacionIdStr.isEmpty()) {
            mensaje = "Error: Falta el ID de la planificación.";
            response.sendRedirect(request.getContextPath() +
                    "/detallePlanificacion?mensaje=" + mensaje);
            return;
        }

        try {
            Long planificacionId = Long.parseLong(planificacionIdStr);

            // Verificar que el usuario actual es el planificador
            Comensal comensalActual = obtenerComensalActual(request);
            if (comensalActual == null) {
                mensaje = "Error: No se pudo identificar al usuario actual.";
                response.sendRedirect(request.getContextPath() +
                        "/detallePlanificacion?id=" + planificacionIdStr + "&mensaje=" + mensaje);
                return;
            }

            Planificacion planificacion = new PlanificacionDAO().obtenerPlanificacionPorId(planificacionId);
            if (!planificacion.esComensalPlanificador(comensalActual)) {
                mensaje = "Error: Solo el planificador puede iniciar la votación.";
                response.sendRedirect(request.getContextPath() +
                        "/detallePlanificacion?id=" + planificacionIdStr + "&mensaje=" + mensaje);
                return;
            }

            try {
                planificacionService.iniciarVotacion(planificacionId);
                mensaje = "La votación ha sido iniciada correctamente.";
            } catch (Exception e) {
                mensaje = "Error: " + e.getMessage();
            }

        } catch (NumberFormatException e) {
            mensaje = "Error: ID de planificación inválido.";
        }

        response.sendRedirect(request.getContextPath() +
                "/detallePlanificacion?id=" + planificacionIdStr + "&mensaje=" + mensaje);
    }

    private void procesarVotar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String planificacionIdStr = request.getParameter("planificacionId");
        String restauranteIdStr = request.getParameter("restauranteId");
        String mensaje;

        System.out.println("planificacionIdStr: " + planificacionIdStr + ", restauranteIdStr: " + restauranteIdStr);

        if (planificacionIdStr == null || planificacionIdStr.isEmpty() ||
                restauranteIdStr == null || restauranteIdStr.isEmpty()) {
            mensaje = "Error: Faltan parámetros requeridos.";
            response.sendRedirect(request.getContextPath() +
                    "/detallePlanificacion?mensaje=" + mensaje);
            return;
        }

        try {
            Long planificacionId = Long.parseLong(planificacionIdStr);
            Long restauranteId = Long.parseLong(restauranteIdStr);

            // Obtener comensal actual de la sesión
            Comensal comensalActual = obtenerComensalActual(request);
            if (comensalActual == null) {
                mensaje = "Error: No se pudo identificar al usuario actual.";
                response.sendRedirect(request.getContextPath() +
                        "/detallePlanificacion?id=" + planificacionIdStr + "&mensaje=" + mensaje);
                return;
            }

            try {
                planificacionService.registrarVoto(planificacionId, comensalActual.getId(), restauranteId);
                mensaje = "Tu voto ha sido registrado correctamente.";
            } catch (Exception e) {
                mensaje = "Error: " + e.getMessage();
            }

        } catch (NumberFormatException e) {
            mensaje = "Error: ID inválido.";
        }

        response.sendRedirect(request.getContextPath() +
                "/detallePlanificacion?id=" + planificacionIdStr + "&mensaje=" + mensaje);
    }

    private void procesarTerminarVotacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String planificacionIdStr = request.getParameter("planificacionId");
        String mensaje;

        if (planificacionIdStr == null || planificacionIdStr.isEmpty()) {
            mensaje = "Error: Falta el ID de la planificación.";
            response.sendRedirect(request.getContextPath() +
                    "/detallePlanificacion?mensaje=" + mensaje);
            return;
        }

        try {
            Long planificacionId = Long.parseLong(planificacionIdStr);

            // Verificar que el usuario actual es el planificador
            Comensal comensalActual = obtenerComensalActual(request);
            if (comensalActual == null) {
                mensaje = "Error: No se pudo identificar al usuario actual.";
                response.sendRedirect(request.getContextPath() +
                        "/detallePlanificacion?id=" + planificacionIdStr + "&mensaje=" + mensaje);
                return;
            }

            Planificacion planificacion = new PlanificacionDAO().obtenerPlanificacionPorId(planificacionId);
            if (!planificacion.esComensalPlanificador(comensalActual)) {
                mensaje = "Error: Solo el planificador puede terminar la votación.";
                response.sendRedirect(request.getContextPath() +
                        "/detallePlanificacion?id=" + planificacionIdStr + "&mensaje=" + mensaje);
                return;
            }

            try {
                planificacionService.terminarVotacion(planificacionId);
                mensaje = "La votación ha sido terminada correctamente.";
            } catch (Exception e) {
                mensaje = "Error: " + e.getMessage();
            }

        } catch (NumberFormatException e) {
            mensaje = "Error: ID de planificación inválido.";
        }

        response.sendRedirect(request.getContextPath() +
                "/detallePlanificacion?id=" + planificacionIdStr + "&mensaje=" + mensaje);
    }

    private void mostrarResultadosVotacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String planificacionIdStr = request.getParameter("planificacionId");

        if (planificacionIdStr == null || planificacionIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/detallePlanificacion");
            return;
        }

        try {
            Long planificacionId = Long.parseLong(planificacionIdStr);
            Planificacion planificacion = new PlanificacionDAO().obtenerPlanificacionPorId(planificacionId);

            if (planificacion == null) {
                response.sendRedirect(request.getContextPath() + "/detallePlanificacion");
                return;
            }

            // Obtener resultados de la votación
            Map<Restaurante, Integer> resultados = votoDAO.contarVotosPorRestaurante(planificacionId);

            // Obtener el voto del comensal actual si existe
            Comensal comensalActual = obtenerComensalActual(request);
            Restaurante restauranteVotado = null;
            if (comensalActual != null) {
                Voto voto = votoDAO.obtenerVotoComensal(planificacionId, comensalActual.getId());
                if (voto != null) {
                    restauranteVotado = voto.getRestaurante();
                }
            }

            // Configurar atributos para la vista
            request.setAttribute("planificacion", planificacion);
            request.setAttribute("resultados", resultados);
            request.setAttribute("restauranteVotado", restauranteVotado);

            // Mostrar la vista de resultados
            request.getRequestDispatcher("detallePlanificacion.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/detallePlanificacion");
        }
    }

    private Comensal obtenerComensalActual(HttpServletRequest request) {
        Object usuario = request.getSession().getAttribute("usuario");
        if (usuario != null && usuario instanceof Comensal) {
            return (Comensal) usuario;
        }
        return null;
    }
}
