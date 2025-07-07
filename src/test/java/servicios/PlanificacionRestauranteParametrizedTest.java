package servicios;

import entidades.Planificacion;
import entidades.Restaurante;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

public class PlanificacionRestauranteParametrizedTest {

    public static Stream<Object[]> datosDePrueba() {
        return Stream.of(
            new Object[]{"Almuerzo UTP", "12:30", "La Cevichería", true},
            new Object[]{"Cena Team", "19:00", null, false},           // Restaurante nulo (inválido)
            new Object[]{"Desayuno", "08:00", "", false}               // Nombre restaurante vacío (inválido)
        );
    }

    @ParameterizedTest
    @MethodSource("datosDePrueba")
    public void testAsociarRestauranteAPlanificacion(String nombrePlanificacion, String hora,
                                                   String nombreRestaurante, boolean esperadoValido) {
        // Configuración
        Planificacion planificacion = new Planificacion(nombrePlanificacion, hora);
        Restaurante restaurante = (nombreRestaurante != null && !nombreRestaurante.isEmpty())
                ? new Restaurante() {{ setNombre(nombreRestaurante); }}
                : null;

        // Ejecución y verificación
        if (!esperadoValido) {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                planificacion.addRestaurante(restaurante);
            });
            assertTrue(exception.getMessage().contains("Restaurante no válido"));
        } else {
            planificacion.addRestaurante(restaurante);
            // Verificar que el último restaurante añadido sea el esperado
            Restaurante ultimoRestaurante = planificacion.getRestaurantes().get(planificacion.getRestaurantes().size() - 1);
            assertEquals(nombreRestaurante, ultimoRestaurante.getNombre());
        }
    }
}