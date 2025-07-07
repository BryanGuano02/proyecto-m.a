package servicios;

import entidades.Calificacion;
import entidades.Comensal;
import entidades.VotoCalificacion;

import java.util.List;
import java.util.Optional;

public class VotacionService {
// 1. Move method (de Votacionservice a CalificacionService)
     public Boolean votarCalificacion(Comensal comensal, Calificacion calificacion) {
         List<VotoCalificacion> votos = calificacion.getVotos();

         Optional<VotoCalificacion> votoExistente = votos.stream().filter(v -> v.getComensal().getId().equals(comensal.getId())).findFirst();

    // 2. Replace Nested Conditional with Guard Clauses
         if (votoExistente.isPresent()) {
             votos.remove(votoExistente.get());
             return false;
         } else {
             VotoCalificacion nuevoVoto = new VotoCalificacion();
             nuevoVoto.setCalificacion(calificacion);
             nuevoVoto.setComensal(comensal);
             votos.add(nuevoVoto);
             return true;
         }
     }

}
