package DAO;

import entidades.Comensal;
import jakarta.persistence.*;

public class ComensalDAO {
    private final EntityManagerFactory emf;

    public ComensalDAO() {
        emf = Persistence.createEntityManagerFactory("UFood_PU");
    }

    public Comensal obtenerComensalPorId(Long idComensal) {
        EntityManager em = emf.createEntityManager();
        try {
            Comensal comensal = em.find(Comensal.class, idComensal);
            return comensal;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
