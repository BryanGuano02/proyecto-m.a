package entidades;

import jakarta.persistence.*;

@Entity
public class Voto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "planificacion_id")
    private Planificacion planificacion;

    @ManyToOne
    @JoinColumn(name = "comensal_id")
    private Comensal comensal;

    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    public Voto() {
    }

    // Constructor con todos los campos
    public Voto(Planificacion planificacion, Comensal comensal, Restaurante restaurante) {
        this.planificacion = planificacion;
        this.comensal = comensal;
        this.restaurante = restaurante;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Planificacion getPlanificacion() {
        return planificacion;
    }

    public void setPlanificacion(Planificacion planificacion) {
        this.planificacion = planificacion;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Voto voto = (Voto) o;

        if (!planificacion.equals(voto.planificacion)) return false;
        return comensal.equals(voto.comensal);
    }

    @Override
    public int hashCode() {
        int result = planificacion.hashCode();
        result = 31 * result + comensal.hashCode();
        return result;
    }
}
