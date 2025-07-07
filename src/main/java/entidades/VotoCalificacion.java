package entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "voto_calificacion")
public class VotoCalificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "calificacion_id")
    private Calificacion calificacion;

    @ManyToOne
    @JoinColumn(name = "comensal_id")
    private Comensal comensal;

    public VotoCalificacion() {
    }

    public Long getId() {
        return id;
    }

    public Calificacion getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Calificacion calificacion) {
        this.calificacion = calificacion;
    }

    public Comensal getComensal() {
        return comensal;
    }

    public void setComensal(Comensal comensal) {
        this.comensal = comensal;
    }

}
