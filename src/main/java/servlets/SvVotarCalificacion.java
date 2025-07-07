package servlets;

import DAO.CalificacionDAO;
import DAO.ComensalDAO;
import entidades.Calificacion;
import entidades.Comensal;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servicios.CalificacionService;

import java.io.IOException;

@WebServlet(name = "SvVotarCalificacion", urlPatterns = { "/votarCalificacion" })
public class SvVotarCalificacion extends HttpServlet {
    private CalificacionDAO calificacionDAO;
    private ComensalDAO comensalDAO;
    private CalificacionService calificacionService;

    @Override
    public void init() throws ServletException {
        calificacionDAO = new CalificacionDAO();
        comensalDAO = new ComensalDAO();
        calificacionService = new CalificacionService(null, null, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idCalificacionStr = request.getParameter("idCalificacion");
        String idComensalStr = request.getParameter("idComensal");
        String redirectUrl = request.getHeader("referer");
        if (redirectUrl == null) {
            redirectUrl = request.getContextPath() + "/detalleRestaurante";
        }
        Long idCalificacion = Long.parseLong(idCalificacionStr);
        Long idComensal = Long.parseLong(idComensalStr);
        Calificacion calificacion = calificacionDAO.obtenerPorId(idCalificacion);
        Comensal comensal = comensalDAO.obtenerComensalPorId(idComensal);
        if (calificacion != null && comensal != null) {

            calificacionService.votarCalificacion(comensal, calificacion);
            calificacionDAO.actualizar(calificacion);
        }

        response.sendRedirect(redirectUrl);
    }
}
// 1
// 2
// 3
