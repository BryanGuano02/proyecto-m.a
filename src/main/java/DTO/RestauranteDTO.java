package DTO;

import entidades.Calificacion;
import entidades.Restaurante;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RestauranteDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String tipoComida;
    private LocalTime horaApertura;
    private LocalTime horaCierre;
    private Double puntajePromedio;
    private int precio;
    private Double distanciaUniversidad;
    private int calidad;
    private int tiempoEspera;
    private boolean estaSuscrito;
    private List<Calificacion> calificaciones;

    public RestauranteDTO() {
    }    public RestauranteDTO(Restaurante restaurante, boolean estaSuscrito) {
        this.id = restaurante.getId();
        this.nombre = restaurante.getNombre();
        this.descripcion = restaurante.getDescripcion();
        this.tipoComida = restaurante.getTipoComida();
        this.horaApertura = restaurante.getHoraApertura();
        this.horaCierre = restaurante.getHoraCierre();
        this.puntajePromedio = restaurante.getPuntajePromedio();
        this.precio = restaurante.getPrecio();
        this.distanciaUniversidad = restaurante.getDistanciaUniversidad();
        this.calidad = restaurante.getCalidad();
        this.tiempoEspera = restaurante.getTiempoEspera();
        this.estaSuscrito = estaSuscrito;
        this.calificaciones = new ArrayList<>(); // La clase Restaurante no tiene getter para calificaciones
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoComida() {
        return tipoComida;
    }

    public void setTipoComida(String tipoComida) {
        this.tipoComida = tipoComida;
    }

    public LocalTime getHoraApertura() {
        return horaApertura;
    }

    public void setHoraApertura(LocalTime horaApertura) {
        this.horaApertura = horaApertura;
    }

    public LocalTime getHoraCierre() {
        return horaCierre;
    }

    public void setHoraCierre(LocalTime horaCierre) {
        this.horaCierre = horaCierre;
    }

    public Double getPuntajePromedio() {
        return puntajePromedio;
    }

    public void setPuntajePromedio(Double puntajePromedio) {
        this.puntajePromedio = puntajePromedio;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public Double getDistanciaUniversidad() {
        return distanciaUniversidad;
    }

    public void setDistanciaUniversidad(Double distanciaUniversidad) {
        this.distanciaUniversidad = distanciaUniversidad;
    }

    public int getCalidad() {
        return calidad;
    }

    public void setCalidad(int calidad) {
        this.calidad = calidad;
    }

    public int getTiempoEspera() {
        return tiempoEspera;
    }

    public void setTiempoEspera(int tiempoEspera) {
        this.tiempoEspera = tiempoEspera;
    }    public boolean isEstaSuscrito() {
        return estaSuscrito;
    }

    public boolean getEstaSuscrito() {
        return estaSuscrito;
    }

    public void setEstaSuscrito(boolean suscrito) {
        this.estaSuscrito = suscrito;
    }


    public List<Calificacion> getCalificaciones() {
        return calificaciones;
    }

    public void setCalificaciones(List<Calificacion> calificaciones) {
        this.calificaciones = calificaciones;
    }
}
