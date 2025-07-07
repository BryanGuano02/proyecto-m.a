package servlets;

import DAO.RestauranteDAO;
import DAO.UsuarioDAO;
import entidades.DuenioRestaurante;
import entidades.Restaurante;
import entidades.Usuario;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalTime;

@WebServlet(name = "SvRestaurante", value = "/restaurante")
public class SvRestaurante extends HttpServlet {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO(Persistence.createEntityManagerFactory("UFood_PU"));
    private final RestauranteDAO restauranteDAO = new RestauranteDAO(Persistence.createEntityManagerFactory("UFood_PU"));
    private EntityManagerFactory emf;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        // Validar sesión
        if (session == null || session.getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // Redirigir según tipo de usuario
        if ("DUENO_RESTAURANTE".equals(usuario.getTipoUsuario())) {
            DuenioRestaurante duenio = (DuenioRestaurante) usuario;
            mostrarPanelRestaurante(req, resp, duenio.getRestaurante());
        } else {
            resp.sendRedirect(req.getContextPath() + "/inicio");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("usuario") == null || !"DUENO_RESTAURANTE".equals(((Usuario) session.getAttribute("usuario")).getTipoUsuario())) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        DuenioRestaurante duenio = (DuenioRestaurante) session.getAttribute("usuario");
        Restaurante restauranteUsuario = duenio.getRestaurante();
        String accion = req.getParameter("accion");

        if ("guardar".equals(accion)) {
            procesarGuardarRestaurante(req, resp, restauranteUsuario);
        } else if ("actualizar".equals(accion)) {
            procesarActualizarRestaurante(req, resp, restauranteUsuario);
        } else {
            resp.sendRedirect(req.getContextPath() + "/crearRestaurante.jsp");
        }

    }
    private void mostrarPanelRestaurante(HttpServletRequest req, HttpServletResponse resp, Restaurante restauranteUsuario) throws ServletException, IOException {
        try {
            String success = req.getParameter("success");
            String error = req.getParameter("error");

            if (success != null) {
                req.setAttribute("successMessage", success);
            }
            if (error != null) {
                req.setAttribute("errorMessage", error);
            }

            Restaurante restaurante = restauranteDAO.obtenerRestaurantePorId(restauranteUsuario.getId());
            req.setAttribute("restaurante", restaurante);

            req.getRequestDispatcher("crearRestaurante.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al cargar datos del restaurante: " + e.getMessage());
            req.getRequestDispatcher("crearRestaurante.jsp").forward(req, resp);
        }
    }

    private void procesarGuardarRestaurante(HttpServletRequest req, HttpServletResponse resp, Restaurante restauranteUsuario) throws IOException {
        try {
            restauranteUsuario.setNombre(req.getParameter("nombre"));
            restauranteUsuario.setDescripcion(req.getParameter("descripcion"));
            restauranteUsuario.setTipoComida(req.getParameter("tipoComida"));
            restauranteUsuario.setHoraApertura(LocalTime.parse(req.getParameter("horaApertura")));
            restauranteUsuario.setHoraCierre(LocalTime.parse(req.getParameter("horaCierre")));
            restauranteUsuario.setDistanciaUniversidad(Double.parseDouble(req.getParameter("distanciaUniversidad")));
            restauranteUsuario.setPrecio(Integer.parseInt(req.getParameter("precio")));

            try {
                restauranteUsuario.setTiempoEspera(Integer.parseInt(req.getParameter("tiempoEspera")));
                restauranteUsuario.setCalidad(Integer.parseInt(req.getParameter("calidad")));
                restauranteUsuario.setPrecio(Integer.parseInt(req.getParameter("precio")));
                restauranteUsuario.setDistanciaUniversidad(Double.parseDouble(req.getParameter("distanciaUniversidad")));
            } catch (NumberFormatException e) {
                resp.sendRedirect(req.getContextPath() + "/restaurante?error=Formato+inválido+en+campos+numéricos");
                return;
            }

            restauranteDAO.save(restauranteUsuario);

            HttpSession session = req.getSession();
            DuenioRestaurante duenio = (DuenioRestaurante) session.getAttribute("usuario");
            duenio.setRestaurante(restauranteUsuario);
            session.setAttribute("usuario", duenio);

            resp.sendRedirect(req.getContextPath() + "/restaurante?success=Restaurante+actualizado+exitosamente");
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/restaurante?error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    private void procesarActualizarRestaurante(HttpServletRequest req, HttpServletResponse resp, Restaurante restauranteUsuario) throws IOException {
        try {
            String nombre = req.getParameter("nombre");
            String descripcion = req.getParameter("descripcion");
            String tipoComida = req.getParameter("tipoComida");
            String horaApertura = req.getParameter("horaApertura");
            String horaCierre = req.getParameter("horaCierre");
            Double distanciaUniversidad = Double.parseDouble(req.getParameter("distanciaUniversidad"));
            int precio = Integer.parseInt(req.getParameter("precio"));

            if (nombre != null) restauranteUsuario.setNombre(nombre);
            if (descripcion != null) restauranteUsuario.setDescripcion(descripcion);
            if (tipoComida != null) restauranteUsuario.setTipoComida(tipoComida);
            if (horaApertura != null) restauranteUsuario.setHoraApertura(LocalTime.parse(horaApertura));
            if (horaCierre != null) restauranteUsuario.setHoraCierre(LocalTime.parse(horaCierre));
            if (distanciaUniversidad != null) restauranteUsuario.setDistanciaUniversidad(distanciaUniversidad);
            if (precio > 0) restauranteUsuario.setPrecio(precio);

            restauranteDAO.save(restauranteUsuario);

            HttpSession session = req.getSession();
            DuenioRestaurante duenio = (DuenioRestaurante) session.getAttribute("usuario");
            duenio.setRestaurante(restauranteUsuario);
            session.setAttribute("usuario", duenio);

            resp.sendRedirect(req.getContextPath() + "/restaurante?success=Restaurante+actualizado+correctamente");
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/restaurante?error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    @Override
    public void destroy() {
        if (usuarioDAO != null) usuarioDAO.close();
        if (emf != null) emf.close();
    }
}
