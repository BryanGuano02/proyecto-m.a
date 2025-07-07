package servicios;

import entidades.Calificacion;
import entidades.Comensal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VotacionCalificacionParametrizedTest {

    static Stream<Arguments> datosDePrueba() {
        return Stream.of(
            Arguments.of(Arrays.asList(1L), 1, "1 voto"),
            Arguments.of(Arrays.asList(1L, 1L), 0, "0 votos"),
            Arguments.of(Arrays.asList(1L, 2L, 3L), 3, "3 votos"),
            Arguments.of(Arrays.asList(1L, 2L, 1L, 2L), 0, "0 votos"),
            Arguments.of(Arrays.asList(1L, 1L, 1L), 1, "1 voto"),
            Arguments.of(Arrays.asList(1L, 2L, 3L, 2L, 1L), 1, "1 voto")
        );
    }

    @ParameterizedTest(name = "{2}") // Usa la descripción como nombre del test
    @MethodSource("datosDePrueba")
    public void testConteoVotos(List<Long> listaIdsComensales,
                              int resultadoEsperado,
                              String descripcion) {
        CalificacionService calificacionService = new CalificacionService(null, null, null);
        Calificacion calificacion = new Calificacion();

        for (Long idComensal : listaIdsComensales) {
            Comensal comensal = new Comensal();
            comensal.setId(idComensal);
            calificacionService.votarCalificacion(comensal, calificacion);
        }

        assertEquals(resultadoEsperado, calificacion.getVotos().size(), descripcion);
    }
}