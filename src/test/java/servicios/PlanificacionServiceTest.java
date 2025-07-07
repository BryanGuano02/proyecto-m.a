package servicios;

import DAO.PlanificacionDAO;
import entidades.Comensal;
import entidades.Planificacion;
import entidades.Restaurante;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlanificacionServiceTest {

    @InjectMocks
    private PlanificacionService planificacionService;

    @Test
    void givenNameAndHour_whenCreatePlanification_thenPlanificationNotNull() {
        // Arrange
        String nombre = "Cena de Fin de Año";
        String hora = "20:00";
        Comensal comensal = new Comensal();

        // Act
        Planificacion planificacion = planificacionService.crearPlanificacion(nombre, hora, comensal);

        // Assert
        assertNotNull(planificacion, "La planificación no debería ser nula");
        assertEquals(nombre, planificacion.getNombre(), "El nombre no coincide");
        assertEquals(hora, planificacion.getHora(), "La hora no coincide");
    }

    @Test
    void givenDiners_whenAddToPlanification_thenSuccess() {
        // Arrange
        Planificacion planificacion = new Planificacion("Almuerzo UTP", "12:30");
        List<Comensal> comensales = Arrays.asList(new Comensal(), new Comensal());

        // Act
        boolean exito = planificacionService.agregarComensales(planificacion, comensales);

        // Assert
        assertTrue(exito, "Debería retornar true al agregar comensales");
        assertEquals(2, planificacion.getComensales().size(), "Debería tener 2 comensales");
    }

    @Test
    void givenDuplicateDiner_whenAddToPlanification_thenThrowException() {
        // Arrange
        Planificacion planificacion = new Planificacion("Almuerzo UTP", "12:30");
        Comensal comensal = new Comensal();
        comensal.setId(1L);
        planificacion.addComensal(comensal);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> planificacion.addComensal(comensal),
            "Debería lanzar IllegalArgumentException");

        assertEquals("El comensal ya está en esta planificación", exception.getMessage());
    }

    @Test
    void givenRestaurant_whenSetToPlanification_thenAssociationOk() {
        // Arrange
        Planificacion planificacion = new Planificacion("Cena de equipo", "19:00");
        Restaurante restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setNombre("La Cevichería");

        // Act
        planificacion.addRestaurante(restaurante);

        // Assert
        assertAll("Verificación de asociación de restaurante",
            () -> assertFalse(planificacion.getRestaurantes().isEmpty(),
                "La lista de restaurantes no debería estar vacía"),
            () -> assertEquals("La Cevichería",
                planificacion.getRestaurantes().get(0).getNombre(),
                "El nombre del restaurante no coincide")
        );
    }

    @Test
    void givenTime_whenCalculateRemainingTime_thenOk() {
        // Arrange
        LocalDateTime ahora = LocalDateTime.of(2025, 5, 12, 12, 30);
        LocalDateTime horaLimite = LocalDateTime.of(2025, 5, 12, 13, 0);

        // Act
        int minutos = planificacionService.calcularMinutosRestantesParaVotacion(ahora, horaLimite);

        // Assert
        assertEquals(30, minutos, "Los minutos restantes no coinciden");
    }

    @Test
    void givenVotes_whenGetMostVotedRestaurant_thenOk() {
        // Arrange
        Map<Restaurante, Integer> votos = new HashMap<>();
        Restaurante restaurante1 = new Restaurante();
        Restaurante restaurante2 = new Restaurante();

        votos.put(restaurante1, 3);
        votos.put(restaurante2, 1);

        // Act
        Restaurante restauranteMasVotado = planificacionService.obtenerRestauranteMasVotado(votos);

        // Assert
        assertAll("Verificación de restaurante más votado",
            () -> assertNotNull(restauranteMasVotado, "El restaurante no debería ser null"),
            () -> assertEquals(restaurante1, restauranteMasVotado,
                "El restaurante más votado no coincide")
        );
    }

    @Test
    void givenTie_whenResolveTie_thenReturnRandomRestaurant() {
        // Arrange
        Restaurante restaurante1 = new Restaurante();
        Restaurante restaurante2 = new Restaurante();

        Map<Restaurante, Integer> votos = new HashMap<>();
        votos.put(restaurante1, 5);
        votos.put(restaurante2, 5);

        // Act
        Restaurante resultado = planificacionService.resolverEmpateEnVotacion(votos);

        // Assert
        assertNotNull(resultado, "El resultado no debería ser null");
        assertTrue(votos.containsKey(resultado),
            "El restaurante devuelto debe estar en la lista de votos");
    }

    @Test
    void givenPlanification_whenCancel_thenStatusChanged() {
        // Arrange
        Comensal comensal = new Comensal();
        Planificacion planificacion = planificacionService.crearPlanificacion("Comida Grupal", "12:00", comensal);

        // Act
        planificacionService.cancelarPlanificacion(planificacion);

        // Assert
        assertEquals("Cancelado", planificacion.getEstado(),
            "El estado debería ser 'Cancelado'");
    }

    @Test
    void givenGoodRestaurant_whenRecommend_thenReturnTrue() {
        // Arrange
        Restaurante restauranteMock = mock(Restaurante.class);
        when(restauranteMock.getPuntajePromedio()).thenReturn(4.0);
        when(restauranteMock.getDistanciaUniversidad()).thenReturn(3.0);
        when(restauranteMock.getTiempoEspera()).thenReturn(20);

        // Act
        boolean esRecomendado = planificacionService.recomendarRestaurante(restauranteMock);

        // Assert
        assertTrue(esRecomendado, "El restaurante debería ser recomendado");
    }
}