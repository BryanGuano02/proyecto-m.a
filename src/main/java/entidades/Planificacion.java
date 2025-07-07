package entidades;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Planificacion {
    public String nombre;
    public String hora;
    public String estado;
    public String estadoVotacion; // "No iniciada", "En progreso", "Terminada"

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "planificacion_comensal", joinColumns = @JoinColumn(name = "planificacion_id"), inverseJoinColumns = @JoinColumn(name = "comensal_id"))
    private List<Comensal> comensales = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "idComensalPlanificador")
    private Comensal comensalPlanificador;

    // Changed to ManyToMany to support multiple restaurants
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "planificacion_restaurante", joinColumns = @JoinColumn(name = "planificacion_id"), inverseJoinColumns = @JoinColumn(name = "restaurante_id"))
    private List<Restaurante> restaurantes = new ArrayList<>();

    // Restaurant with the highest number of votes
    @ManyToOne
    @JoinColumn(name = "restaurante_ganador_id")
    private Restaurante restauranteGanador;

    public Planificacion() {
    }

    public Planificacion(String nombre, String hora) {
        this.nombre = nombre;
        this.hora = hora;
        estado = "Activa";
        estadoVotacion = "No iniciada";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public List<Comensal> getComensales() {
        return comensales;
    }

    public void setComensales(List<Comensal> comensales) {
        this.comensales = comensales;
    }

    public List<Restaurante> getRestaurantes() {
        if (this.restaurantes == null) {
            this.restaurantes = new ArrayList<>();
        }
        return restaurantes;
    }

    public void setRestaurantes(List<Restaurante> restaurantes) {
        this.restaurantes = restaurantes;
    }

    public void addRestaurante(Restaurante restaurante) {
        if (restaurante == null || restaurante.getNombre() == null || restaurante.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("Restaurante no válido");
        }
        if (this.restaurantes == null) {
            this.restaurantes = new ArrayList<>();
        }
        if (!this.restaurantes.contains(restaurante)) {
            this.restaurantes.add(restaurante);
        }
    }

    public void addComensal(Comensal comensal) {
        if (this.comensales == null) {
            this.comensales = new ArrayList<>();
        }
        if (this.comensales.contains(comensal)) {
            throw new IllegalArgumentException("El comensal ya está en esta planificación");
        }
        this.comensales.add(comensal);
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Comensal getComensalPlanificador() {
        return comensalPlanificador;
    }

    public void setComensalPlanificador(Comensal comensalPlanificador) {
        this.comensalPlanificador = comensalPlanificador;
    }

    // Nuevos métodos para votación
    public String getEstadoVotacion() {
        return estadoVotacion;
    }

    public void setEstadoVotacion(String estadoVotacion) {
        this.estadoVotacion = estadoVotacion;
    }

    public void iniciarVotacion() {
        if (!"Activa".equals(this.estado)) {
            throw new IllegalStateException("Solo se puede iniciar votación en una planificación activa");
        }
        if (this.restaurantes == null || this.restaurantes.isEmpty()) {
            throw new IllegalStateException(
                    "La planificación debe tener al menos un restaurante para iniciar votación");
        }
        this.estadoVotacion = "En progreso";
    }

    public void terminarVotacion() {
        if (!"En progreso".equals(this.estadoVotacion)) {
            throw new IllegalStateException("No hay votación en progreso para terminar");
        }
        this.estadoVotacion = "Terminada";
    }

    public Restaurante getRestauranteGanador() {
        return restauranteGanador;
    }

    public void setRestauranteGanador(Restaurante restauranteGanador) {
        this.restauranteGanador = restauranteGanador;
    }

    public boolean esComensalPlanificador(Comensal comensal) {
        if (comensal == null || this.comensalPlanificador == null) {
            return false;
        }
        return comensal.getId().equals(this.comensalPlanificador.getId());
    }

    public boolean puedeVotar(Comensal comensal) {
        if (comensal == null) {
            System.out.println("Error: Comensal es null");
            return false;
        }

        boolean esParticipante = false;
        if (this.comensales != null) {
            for (Comensal c : this.comensales) {
                if (c.getId() != null && c.getId().equals(comensal.getId())) {
                    esParticipante = true;
                    break;
                }
            }
        }

        boolean esPlanificador = esComensalPlanificador(comensal);
        boolean enProgresoVotacion = "En progreso".equals(this.estadoVotacion);

        return enProgresoVotacion && (esParticipante || esPlanificador);
    }
}
