package servicios;

import DAO.PlanificacionDAO;
import entidades.Restaurante;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class PlanificacionSugerirParametrizedTest {

    public static Stream<Object[]> datosDePrueba() {
        return Stream.of(
            new Object[]{4.5, 1.0, 15, true},
            new Object[]{3.8, 2.5, 20, true},
            new Object[]{2.0, 1.0, 10, false},
            new Object[]{4.0, 10.0, 15, false},
            new Object[]{4.2, 1.5, 40, false}
        );
    }

    @ParameterizedTest
    @MethodSource("datosDePrueba")
    public void sugerirRestaurante(Double puntajePromedio, Double distanciaUniversidad,
                                 int tiempoEspera, boolean esperadoRecomendado) {
        Restaurante restaurante = new Restaurante();
        restaurante.setPuntajePromedio(puntajePromedio);
        restaurante.setDistanciaUniversidad(distanciaUniversidad);
        restaurante.setTiempoEspera(tiempoEspera);

        PlanificacionService planificacion = new PlanificacionService((PlanificacionDAO) null);
        boolean esRecomendado = planificacion.recomendarRestaurante(restaurante);

        assertEquals(esperadoRecomendado, esRecomendado);
    }
}