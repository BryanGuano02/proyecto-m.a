package servlets;

import DAO.DuenioRestauranteDAO;
import DAO.UsuarioDAO;
import entidades.Comensal;
import entidades.DuenioRestaurante;
import entidades.Restaurante;
import entidades.Usuario;
import exceptions.ServiceException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import servicios.AuthService;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "SvAuth", urlPatterns = { "/login", "/registro-restaurante", "/registro-comensal" })
public class SvAuth extends HttpServlet {
    private AuthService authService;
    private static EntityManagerFactory emf;
    private UsuarioDAO usuarioDAO;
    private DuenioRestauranteDAO duenioRestauranteDAO;

    @Override
    public void init() throws ServletException {
        try {
            if (emf == null) {
                emf = Persistence.createEntityManagerFactory("UFood_PU");
            }
            this.usuarioDAO = new UsuarioDAO(emf);
            this.duenioRestauranteDAO = new DuenioRestauranteDAO(emf);
            this.authService = new AuthService(usuarioDAO, duenioRestauranteDAO);
        } catch (Exception e) {
            throw new ServletException("Error al inicializar JPA", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        crearUsuarios();
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        switch (path) {
            case "/login":
                handleLogin(request, response);
                break;
            case "/registro-restaurante":
                handleRestauranteRegistration(request, response);
                break;
            case "/registro-comensal":
                handleComensalRegistration(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nombreUsuario = request.getParameter("nombreUsuario");
        String contrasena = request.getParameter("contrasena");

        try {
            Usuario usuario = authService.login(nombreUsuario, contrasena);
            HttpSession session = request.getSession();
            session.setAttribute("usuario", usuario);

            if (usuario instanceof Comensal) {
                response.sendRedirect("inicio");
            } else if (usuario instanceof DuenioRestaurante) {
                DuenioRestaurante duenio = (DuenioRestaurante) usuario;
                if(duenio.getRestaurante() == null) {
                    duenio.setRestaurante(new Restaurante());
                }
                session.setAttribute("restaurante", duenio.getRestaurante());
                response.sendRedirect("crearRestaurante.jsp");
            }
        } catch (ServiceException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private void handleRestauranteRegistration(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            DuenioRestaurante duenio = new DuenioRestaurante(
                    request.getParameter("nombreUsuario"),
                    request.getParameter("contrasena"),
                    request.getParameter("email")
            );

            // Crear restaurante asociado con datos básicos usando el constructor adecuado
            String nombreRestaurante = request.getParameter("nombre");
            String tipoComida = request.getParameter("tipoComida");
            Restaurante restaurante = new Restaurante(nombreRestaurante, tipoComida);

            // Establecer la relación bidireccional
            duenio.setRestaurante(restaurante);

            authService.registrarDuenioRestaurante(duenio);
            response.sendRedirect("login.jsp?registroExitoso=true");
        } catch (ServiceException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/registro-restaurante.jsp").forward(request, response);
        }
    }

    private void handleComensalRegistration(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String tipoComidaFavorita = request.getParameter("tipoComidaFavorita");
            if (tipoComidaFavorita == null || tipoComidaFavorita.trim().isEmpty()) {
                tipoComidaFavorita = "General";
            }

            Comensal comensal = new Comensal();
            comensal.setNombreUsuario(request.getParameter("nombreUsuario"));
            comensal.setContrasena(request.getParameter("contrasena"));
            comensal.setEmail(request.getParameter("email"));
            comensal.setTipoComidaFavorita(tipoComidaFavorita);
            comensal.setTipoUsuario("COMENSAL");

            authService.registrarComensal(comensal, tipoComidaFavorita);
            response.sendRedirect("login.jsp?registroExitoso=true");
        } catch (ServiceException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
        }
    }

    private void crearUsuarios() {
        try {
            int numeroComensales = 3;
            int numeroRestaurantes = 6;

            crearComensales(numeroComensales);
            crearDueniosRestaurantes(numeroRestaurantes);
        } catch (ServiceException e) {
            System.out.println("Error al crear usuarios: " + e.getMessage());
        }
    }

    private void crearDueniosRestaurantes(int numeroRestaurantes) throws ServiceException {
        List<String> nombresRestaurantes = Arrays.asList(
                "Burger Place", "Comida Casera Doña Marta", "Pescados y Mariscos del Pacífico",
                "Restaurante Gourmet La Mesa", "Pizza Rápida", "El Buen Sabor");

        List<String> tiposComida = Arrays.asList(
                "Comida Rápida", "Comida Casera", "Comida Costeña",
                "Platos a la Carta", "Comida Rápida", "Comida Casera");

        for (int i = 1; i <= numeroRestaurantes; i++) {
            String nombreUsuario = "r" + i;
            String contrasena = "r" + i;
            String email = "r" + i + "@r" + i + ".com";

            if (authService.usuarioExiste(nombreUsuario)) {
                continue;
            }

            DuenioRestaurante duenio = new DuenioRestaurante(nombreUsuario, contrasena, email);

            // Crear el restaurante asociado al dueño
            Restaurante restaurante = new Restaurante(nombresRestaurantes.get(i-1), tiposComida.get(i-1));

            // Establecer la relación bidireccional
            duenio.setRestaurante(restaurante);

            authService.registrarDuenioRestaurante(duenio);
            registrarRestaurantesQuemados(duenio);
        }
    }

    private void crearComensales(int numeroComensales) throws ServiceException {
        for (int i = 1; i <= numeroComensales; i++) {
            String nombreUsuario = "c" + i;
            String contrasena = "c" + i;
            String email = "c" + i + "@c" + i + ".com";
            String tipoComidaFavorita = "Comida Rápida";

            if (authService.usuarioExiste(nombreUsuario)) {
                continue;
            }

            Comensal comensal = new Comensal();
            comensal.setNombreUsuario(nombreUsuario);
            comensal.setContrasena(contrasena);
            comensal.setEmail(email);
            comensal.setTipoComidaFavorita(tipoComidaFavorita);

            authService.registrarComensal(comensal, tipoComidaFavorita);
        }
    }

    private void registrarRestaurantesQuemados(DuenioRestaurante duenio) {
        List<String> descripciones = Arrays.asList(
                "Las mejores hamburguesas de la ciudad", "Comida casera como la de mamá",
                "Los mejores mariscos frescos", "Ambiente elegante y platos selectos",
                "Pizza rápida y deliciosa", "Sabores tradicionales");

        List<String> horasApertura = Arrays.asList(
                "10:00", "08:00", "11:00", "12:00", "11:00", "07:30");

        List<String> horasCierre = Arrays.asList(
                "22:00", "18:00", "23:00", "00:00", "22:30", "17:00");

        List<Double> distanciasUniversidad = Arrays.asList(
                0.5, 1.2, 2.0, 3.5, 0.8, 1.7);

        List<Integer> precios = Arrays.asList(
                2, 2, 4, 5, 1, 2);

        List<Integer> tiemposEspera = Arrays.asList(
                10, 25, 30, 40, 15, 20);

        List<Integer> calidades = Arrays.asList(
                3, 4, 4, 5, 3, 4);

        try {
            Restaurante restaurante = duenio.getRestaurante();
            if (restaurante == null) {
                System.out.println("Error: Dueño sin restaurante asociado: " + duenio.getNombreUsuario());
                return;
            }

            int index = Integer.parseInt(duenio.getNombreUsuario().substring(1)) - 1;

            restaurante.setDescripcion(descripciones.get(index));
            restaurante.setHoraApertura(LocalTime.parse(horasApertura.get(index)));
            restaurante.setHoraCierre(LocalTime.parse(horasCierre.get(index)));
            restaurante.setDistanciaUniversidad(distanciasUniversidad.get(index));
            restaurante.setPrecio(precios.get(index));
            restaurante.setTiempoEspera(tiemposEspera.get(index));
            restaurante.setCalidad(calidades.get(index));

            duenioRestauranteDAO.save(duenio);
            System.out.println("Restaurante actualizado: " + duenio.getNombreUsuario() + " - " + restaurante.getNombre());

        } catch (Exception e) {
            System.out.println("Error al actualizar restaurante: " + e.getMessage());
        }
    }

    @Override
    public void destroy() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
