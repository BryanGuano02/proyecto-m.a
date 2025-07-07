package servicios;

import DAO.CalificacionDAO;
import DAO.RestauranteDAO;
import DAO.UsuarioDAO;
import entidades.Calificacion;
import entidades.Comensal;
import entidades.Restaurante;
import entidades.VotoCalificacion;
import exceptions.ServiceException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.*;
import java.util.stream.Collectors;

public class CalificacionService {
    private final CalificacionDAO calificacionDAO;
    private final UsuarioDAO usuarioDAO;
    private final RestauranteDAO restauranteDAO;
    private final EntityManagerFactory emf;

    public CalificacionService(CalificacionDAO calificacionDAO, UsuarioDAO usuarioDAO, RestauranteDAO restauranteDAO) {
        if (usuarioDAO == null && calificacionDAO == null && restauranteDAO == null) {
            this.emf = null;
        } else {
            this.emf = Persistence.createEntityManagerFactory("UFood_PU");
        }
        this.calificacionDAO = calificacionDAO;
        this.usuarioDAO = usuarioDAO;
        this.restauranteDAO = restauranteDAO;
    }

    public void crearCalificacion(Calificacion calificacion) throws ServiceException {
        calcularPuntajeCalificacion(calificacion);
        try {
            if (!calificacionDAO.crear(calificacion)) {
                throw new ServiceException("No se pudo crear la calificación");
            }
//            actualizarPuntajePromedio(calificacion.getRestaurante());
        } catch (Exception e) {
            throw new ServiceException("Error al crear calificación: " + e.getMessage(), e);
        }
    }

//    public void calificar(Map<String, Object> parametrosCalificacion) throws ServiceException {
//        try {
//            //Refactorizacion
//            // Extract Method
//

    /// /            // Double puntaje = (Double) parametrosCalificacion.get("puntaje");
    /// /            int calidadComida = (Integer) parametrosCalificacion.get("calidadComida");
    /// /            int calidadServicio = (Integer) parametrosCalificacion.get("calidadServicio");
    /// /            int limpieza = (Integer) parametrosCalificacion.get("limpieza");
    /// /            int ambiente = (Integer) parametrosCalificacion.get("ambiente");
    /// /            int tiempoEspera = (Integer) parametrosCalificacion.get("tiempoEspera");
    /// /            int relacionPrecioCalidad = (Integer) parametrosCalificacion.get("relacionPrecioCalidad");
    /// /            int variedadMenu = (Integer) parametrosCalificacion.get("variedadMenu");
    /// /            int accesibilidad = (Integer) parametrosCalificacion.get("accesibilidad");
    /// /            int volveria = (Integer) parametrosCalificacion.get("volveria");
    /// /            String comentario = (String) parametrosCalificacion.get("comentario");
    /// /            Long idComensal = (Long) parametrosCalificacion.get("idComensal");
    /// /            Long idRestaurante = (Long) parametrosCalificacion.get("idRestaurante");
//
//            Calificacion calificacion = extraerParametrosCalificacion(parametrosCalificacion);
//
//            Comensal comensal = usuarioDAO.obtenerComensalPorId(calificacion.getComensal().getId());
//            Restaurante restaurante = restauranteDAO.obtenerRestaurantePorId(calificacion.getRestaurante().getId());
//
//            if (calificacion.getComensal() == null || calificacion.getRestaurante() == null) {
//                throw new ServiceException("Comensal o restaurante no encontrado");
//            }
//
//            calificacion.setComensal(comensal);
//            calificacion.setRestaurante(restaurante);
//
//            Calificacion calificacionExistente = calificacionDAO.obtenerCalificacionPorComensalYRestaurante(comensal.getId(), restaurante.getId());
//
//            if (calificacionExistente != null) {
//                calificacionExistente.setPuntaje(puntaje);
//                calificacionExistente.setComentario(comentario);
//                calificacionExistente.setComensal(comensal);
//                calificacionExistente.setRestaurante(restaurante);
//                calcularPuntajeCalificacion(calificacionExistente);
//
//                if (!calificacionDAO.actualizar(calificacionExistente)) {
//                    throw new ServiceException("No se pudo actualizar la calificación existente");
//                }
//            } else {
//                crearCalificacion(calificacion);
//            }
//            actualizarPuntajePromedio(restaurante);
//        } catch (Exception e) {
//            throw new ServiceException("Error al procesar la calificación: " + e.getMessage(), e);
//        }
//    }
    public void calificar(Map<String, Object> parametrosCalificacion) throws ServiceException {
        try {
            Double puntaje = (Double) parametrosCalificacion.get("puntaje");
            String comentario = (String) parametrosCalificacion.get("comentario");
            Long idComensal = (Long) parametrosCalificacion.get("idComensal");
            Long idRestaurante = (Long) parametrosCalificacion.get("idRestaurante");

            Calificacion calificacion = extraerParametrosCalificacion(parametrosCalificacion);

            Comensal comensal = usuarioDAO.obtenerComensalPorId(idComensal);
            Restaurante restaurante = restauranteDAO.obtenerRestaurantePorId(calificacion.getRestaurante().getId());

            if (comensal == null || restaurante == null) {
                throw new ServiceException("Comensal o restaurante no encontrado");
            }

            Calificacion calificacionExistente = calificacionDAO.obtenerCalificacionPorComensalYRestaurante(idComensal, idRestaurante);

            if (calificacionExistente != null) {

                System.out.println("Calificación id existente: " + calificacionExistente.getId() + " - comentario:" + calificacionExistente.getComentario());
                calificacionExistente.setPuntaje(puntaje);
                calificacionExistente.setComentario(comentario);

                if (!calificacionDAO.actualizar(calificacionExistente)) {
                    throw new ServiceException("No se pudo actualizar la calificación existente");
                }

                actualizarPuntajePromedio(restaurante);
            } else {
                Calificacion nuevaCalificacion = new Calificacion();
                nuevaCalificacion.setPuntaje(puntaje);
                nuevaCalificacion.setComentario(comentario);
                nuevaCalificacion.setComensal(comensal);
                nuevaCalificacion.setRestaurante(restaurante);

                crearCalificacion(nuevaCalificacion);
            }
        } catch (Exception e) {
            throw new ServiceException("Error al procesar la calificación: " + e.getMessage(), e);
        }
    }

    public Calificacion extraerParametrosCalificacion(Map<String, Object> parametros) {
        Double puntaje = (Double) parametros.get("puntaje");
        String comentario = (String) parametros.get("comentario");
        Long idComensal = (Long) parametros.get("idComensal");
        Long idRestaurante = (Long) parametros.get("idRestaurante");

        Comensal comensal = new Comensal();
        comensal.setId(idComensal);

        Restaurante restaurante = new Restaurante();
        restaurante.setId(idRestaurante);

        return new Calificacion(puntaje, comentario, comensal, restaurante);
    }

    public double calcularPuntajeCalificacion(Calificacion calificacion) {
        int[] puntajes = calificacion.obtenerPuntajes();
        int suma = Arrays.stream(puntajes).sum();
        double promedio = (double) suma / puntajes.length;
        calificacion.setPuntaje(promedio);
        return promedio;
    }

    public void actualizarPuntajePromedio(Restaurante restaurante) {
        try {
            Double nuevoPromedio = calificacionDAO.calcularPromedioCalificaciones(restaurante.getId());
            restaurante.setPuntajePromedio(nuevoPromedio);
            restauranteDAO.save(restaurante);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar promedio", e);
        }
    }

    // 1. Move method (de Votacionservice a CalificacionService)
    public Boolean votarCalificacion(Comensal comensal, Calificacion calificacion) {
        List<VotoCalificacion> votos = calificacion.getVotos();
        // 3. Extract method
        Optional<VotoCalificacion> votoExistente = encontrarVotoExistente(votos, comensal);

        // 2. Replace Nested Conditional with Guard Clauses
        if (votoExistente.isPresent()) {
            votos.remove(votoExistente.get());
            return false;
        }

        // 3. Extract method
        agregarNuevoVoto(votos, comensal, calificacion);
        return true;
    }

    // 3. Extract method
    private Optional<VotoCalificacion> encontrarVotoExistente(List<VotoCalificacion> votos, Comensal comensal) {
        return votos.stream().filter(v -> v.getComensal().getId().equals(comensal.getId())).findFirst();
    }

    // 3. Extract method
    private void agregarNuevoVoto(List<VotoCalificacion> votos, Comensal comensal, Calificacion calificacion) {
        VotoCalificacion nuevoVoto = new VotoCalificacion();
        nuevoVoto.setCalificacion(calificacion);
        nuevoVoto.setComensal(comensal);
        votos.add(nuevoVoto);
    }

//    public Calificacion extraerParametrosCalificacion(Map<String, Object> parametros) {
//        String comentario = (String) parametros.get("comentario");
//        Long idComensal = (Long) parametros.get("idComensal");
//        Long idRestaurante = (Long) parametros.get("idRestaurante");
//        int calidadComida = (Integer) parametros.get("calidadComida");
//        int calidadServicio = (Integer) parametros.get("calidadServicio");
//        int limpieza = (Integer) parametros.get("limpieza");
//        int ambiente = (Integer) parametros.get("ambiente");
//        int tiempoEspera = (Integer) parametros.get("tiempoEspera");
//        int relacionPrecioCalidad = (Integer) parametros.get("relacionPrecioCalidad");
//        int variedadMenu = (Integer) parametros.get("variedadMenu");
//        int accesibilidad = (Integer) parametros.get("accesibilidad");
//        int volveria = (Integer) parametros.get("volveria");
//
//        Comensal comensal = new Comensal();
//        comensal.setId(idComensal);
//
//        Restaurante restaurante = new Restaurante();
//        restaurante.setId(idRestaurante);
//
//        return new Calificacion(puntaje);
//    }

    public List<Calificacion> ordenarCalificacionesPorVotos(List<Calificacion> calificaciones) {
        if (calificaciones == null) {
            throw new IllegalArgumentException("La lista de calificaciones no puede ser nula");
        }

        return calificaciones.stream().sorted(Comparator.comparingInt((Calificacion c) -> c.getVotos().size()).reversed()).collect(Collectors.toList());
    }

    public List<Calificacion> obtenerCalificacionesPorRestaurante(Long restauranteId) {
        try {
            return calificacionDAO.obtenerCalificacionesPorRestaurante(restauranteId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener calificaciones", e);
        }
    }
}
