package entidades;

import jakarta.persistence.*;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "comensales")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Comensal extends Usuario {

    @ManyToMany(mappedBy = "comensales")
    private List<Planificacion> planificaciones;

    @Column(name = "tipo_comida_favorita")
    private String tipoComidaFavorita;

    public Comensal() {
        this.setTipoUsuario("COMENSAL");
    }


    public Comensal(String nombreUsuario, String contrasena, String email) {
        this.setNombreUsuario(nombreUsuario);
        this.setContrasena(contrasena);
        this.setEmail(email);
        this.setTipoUsuario("COMENSAL");
        this.planificaciones = null;
    }

    public String getTipoComidaFavorita() {
        return tipoComidaFavorita;
    }

    public void setTipoComidaFavorita(String tipoComidaFavorita) {
        this.tipoComidaFavorita = tipoComidaFavorita;
    }


    public List<Planificacion> getPlanificaciones() {
        return planificaciones;
    }

    public void setPlanificaciones(List<Planificacion> planificaciones) {
        this.planificaciones = planificaciones;
    }

    @Override
    public String toString() {
        return "Comensal: " + super.getId();
    }
}
