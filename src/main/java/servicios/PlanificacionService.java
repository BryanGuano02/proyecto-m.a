package servicios;

import DAO.PlanificacionDAO;
import DAO.RestauranteDAO;
import DAO.UsuarioDAO;
import DAO.VotoDAO;
import entidades.Comensal;
import entidades.Planificacion;
import entidades.Restaurante;
import entidades.Voto;
import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class PlanificacionService {
    private final PlanificacionDAO planificacionDAO;
    private UsuarioDAO usuarioDAO;
    private RestauranteDAO restauranteDAO;
    private VotoDAO votoDAO;
    private EntityManagerFactory emf;

    public PlanificacionService(PlanificacionDAO planificacionDAO) {
        if (planificacionDAO == null) {
            this.emf = null;
        } else {
            this.emf = Persistence.createEntityManagerFactory("UFood_PU");
        }
        this.planificacionDAO = planificacionDAO;
        this.usuarioDAO = new UsuarioDAO(emf);
        this.votoDAO = new VotoDAO(emf);
        this.restauranteDAO = new RestauranteDAO(emf);
    }

    public Planificacion crearPlanificacion(String nombre, String hora, Comensal comensal) {
        validarParametrosCreacion(nombre, hora);
        Planificacion planificacion = new Planificacion(nombre, hora);
        if (comensal == null) {
            throw new IllegalArgumentException("Comensal no encontrado");
        }
        planificacion.setComensalPlanificador(comensal);
        return planificacion;
    }

    public void guardarPlanificacion(Planificacion planificacion) {
        planificacionDAO.save(planificacion);
    }

    private void validarParametrosCreacion(String nombre, String hora) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es requerido");
        }
        if (hora == null || hora.trim().isEmpty()) {
            throw new IllegalArgumentException("La hora es requerida");
        }
    }

    public Boolean agregarComensales(Planificacion planificacion, List<Comensal> comensales) {
        for (Comensal comensal : comensales) {
            if (comensal != null) {
                planificacion.addComensal(comensal);
            }
        }
        return true;
    }

    private Planificacion findPlanificacion(EntityManager em, Long planificacionId) {
        Planificacion planificacion = em.find(Planificacion.class, planificacionId);
        if (planificacion == null) {
            throw new EntityNotFoundException("Planificación no encontrada con ID: " + planificacionId);
        }
        return planificacion;
    }

    public Boolean recomendarRestaurante(Restaurante restaurante) {
        final Double PUNTAJE_MINIMO = 3.5;
        final Double DISTANCIA_MAXIMA = 5.0;
        final int TIEMPO_MAXIMO_ESPERA = 30;

        if (restaurante == null) {
            throw new IllegalArgumentException("El restaurante no puede ser nulo");
        }
        if (restaurante.getPuntajePromedio() == null || restaurante.getDistanciaUniversidad() == null
                || restaurante.getTiempoEspera() == 0) {
            throw new IllegalArgumentException("Los atributos del restaurante no pueden ser nulos");
        }

        return restaurante.getPuntajePromedio() >= PUNTAJE_MINIMO
                && restaurante.getDistanciaUniversidad() <= DISTANCIA_MAXIMA
                && restaurante.getTiempoEspera() <= TIEMPO_MAXIMO_ESPERA;
    }

    public int calcularMinutosRestantesParaVotacion(LocalDateTime ahora, LocalDateTime horaLimite) {

        return (int) Duration.between(ahora, horaLimite).toMinutes();
    }

    public Restaurante obtenerRestauranteMasVotado(Map<Restaurante, Integer> votos) {

        return votos.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

    }

    public void cancelarPlanificacion(Planificacion planificacion) {

        if (planificacion == null) {
            throw new IllegalArgumentException("La planificación no puede ser nula");
        }
        if (!"Activa".equalsIgnoreCase(planificacion.getEstado())) {
            throw new IllegalStateException("Solo se puede cancelar una planificación activa");
        }
        planificacion.setEstado("Cancelado");
    }

    public Restaurante resolverEmpateEnVotacion(Map<Restaurante, Integer> votos) {
        int maxVotos = votos.values().stream().max(Integer::compare).orElse(0);
        List<Restaurante> empatados = votos.entrySet().stream()
                .filter(entry -> entry.getValue() == maxVotos)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (empatados.size() > 1) {
            Random random = new Random();
            return empatados.get(random.nextInt(empatados.size()));
        }
        return empatados.isEmpty() ? null : empatados.get(0);
    }

    public void confirmarRestauranteDelGrupo(Planificacion planificacion, String restauranteConfirmado) {
        List<Comensal> comensales = planificacion.getComensales();
        for (Comensal comensal : comensales) {
//            notificacionService.notificarRestauranteElegido(comensal, "Se ha confirmado: " + restauranteConfirmado);
        }
    }

    // Nuevos métodos para votación
    public void iniciarVotacion(Long planificacionId) {
        Planificacion planificacion = planificacionDAO.obtenerPlanificacionPorId(planificacionId);
        if (planificacion == null) {
            throw new EntityNotFoundException("Planificación no encontrada con ID: " + planificacionId);
        }
        planificacion.iniciarVotacion();
        planificacionDAO.save(planificacion);

        // Notificar a todos los comensales que ha iniciado la votación
//        List<Comensal> comensales = planificacion.getComensales();
//        if (notificacionService != null && comensales != null) {
//            for (Comensal comensal : comensales) {
//                notificacionService.notificarRestauranteElegido(comensal,
//                    "Se ha iniciado la votación para la planificación: " + planificacion.getNombre());
//            }
//        }
    }

    public void terminarVotacion(Long planificacionId) {
        Planificacion planificacion = planificacionDAO.obtenerPlanificacionPorId(planificacionId);
        if (planificacion == null) {
            throw new EntityNotFoundException("Planificación no encontrada con ID: " + planificacionId);
        }

        // Contar votos y determinar restaurante ganador
        Map<Restaurante, Integer> conteoVotos = votoDAO.contarVotosPorRestaurante(planificacionId);
        if (conteoVotos != null && !conteoVotos.isEmpty()) {
            Restaurante restauranteGanador = resolverEmpateEnVotacion(conteoVotos);
            planificacion.setRestauranteGanador(restauranteGanador);

//            if (notificacionService != null && planificacion.getComensales() != null) {
//                for (Comensal comensal : planificacion.getComensales()) {
//                    notificacionService.notificarRestauranteElegido(comensal,
//                        "El restaurante ganador para la planificación " +
//                        planificacion.getNombre() + " es: " + restauranteGanador.getNombre());
//                }
//            }
        }

        planificacion.terminarVotacion();
        planificacionDAO.save(planificacion);
    }

    public void registrarVoto(Long planificacionId, Long comensalId, Long idRestaurante) {
        Planificacion planificacion = planificacionDAO.obtenerPlanificacionPorId(planificacionId);
        Comensal comensal = usuarioDAO.obtenerComensalPorId(comensalId);

        Restaurante restaurante = restauranteDAO.obtenerRestaurantePorId(idRestaurante);
//        Restaurante restaurante = (Restaurante) usuarioDAO.findById(restauranteId);

        if (planificacion == null) {
            throw new EntityNotFoundException("Planificación no encontrada con ID: " + planificacionId);
        }

        if (comensal == null) {
            throw new EntityNotFoundException("Comensal no encontrado con ID: " + comensalId);
        }

        if (restaurante == null) {
            throw new EntityNotFoundException("Restaurante no encontrado con ID: " + idRestaurante);
        }

        if (!planificacion.puedeVotar(comensal)) {
            throw new IllegalStateException("El comensal no puede votar en esta planificación");
        }
       System.out.println("Restaurante: " + restaurante.getId());
        System.out.println("Restaurantes dentro de la planificación: " + planificacion.getRestaurantes().size());
        System.out.println(planificacion.getRestaurantes().contains(restaurante));

        // Imprimir el nombre de cada restaurante en la planificación
        System.out.println("Lista de restaurantes en la planificación:");
        for (Restaurante rest : planificacion.getRestaurantes()) {
            System.out.println(" - " + rest.getNombre() + " ID: " + rest.getId());
        }

        // Verificar si el restaurante está en la lista de opciones
        // Verificar si el comensal ya votó y eliminar el voto anterior si existe
        Voto votoExistente = votoDAO.obtenerVotoComensal(planificacionId, comensalId);
        if (votoExistente != null) {
            votoDAO.delete(votoExistente);
        }

        // Crear y guardar el nuevo voto
        Voto voto = new Voto(planificacion, comensal, restaurante);
        votoDAO.save(voto);
    }

    public Map<Restaurante, Integer> obtenerResultadosVotacion(Long planificacionId) {
        // Verificar que la planificación existe
        Planificacion planificacion = planificacionDAO.obtenerPlanificacionPorId(planificacionId);
        if (planificacion == null) {
            throw new EntityNotFoundException("Planificación no encontrada con ID: " + planificacionId);
        }

        return votoDAO.contarVotosPorRestaurante(planificacionId);
    }

    public Restaurante obtenerVotoComensal(Long planificacionId, Long comensalId) {
        Voto voto = votoDAO.obtenerVotoComensal(planificacionId, comensalId);
        return voto != null ? voto.getRestaurante() : null;
    }
}
