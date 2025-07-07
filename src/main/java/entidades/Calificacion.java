package entidades;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    private Double puntaje;
    private String comentario;
    @ManyToOne
    @JoinColumn(name = "idComensal")
    private Comensal comensal;
    @ManyToOne
    @JoinColumn(name = "idRestaurante")
    private Restaurante restaurante;

    @OneToMany(mappedBy = "calificacion", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<VotoCalificacion> votos = new ArrayList<>();

    public Calificacion() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public Calificacion(Double puntaje, String comentario, Comensal comensal, Restaurante restaurante) {
        this.puntaje = puntaje;
        this.comentario = comentario;
        this.comensal = comensal;
        this.restaurante = restaurante;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<VotoCalificacion> getVotos() {
        return votos;
    }

    public Double getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(Double puntaje) {
        this.puntaje = puntaje;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Comensal getComensal() {
        return comensal;
    }

    public void setComensal(Comensal comensal) {
        this.comensal = comensal;
    }

    public Restaurante getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(Restaurante restaurante) {
        this.restaurante = restaurante;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public String getFechaFormateada() {
        return this.fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public int[] obtenerPuntajes() {
        return new int[]{(puntaje != null) ? puntaje.intValue() : 0};
    }

    @Override
    public String toString() {
        return "Calificacion{" + "id=" + id + ", puntaje=" + puntaje + ", comentario='" + comentario + '\'' + ", comensal=" + comensal + ", restaurante=" + restaurante + '}';
    }
}
