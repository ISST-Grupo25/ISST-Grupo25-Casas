// ReservaRepository.java -----------------------------------------------------------
package com.isst.ISST_Grupo25_Casas.repository;

import com.isst.ISST_Grupo25_Casas.models.Gestor;
import com.isst.ISST_Grupo25_Casas.models.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;


public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByHuespedesId(Long huespedId);
    List<Reserva> findByCerraduraId(Long cerraduraId);
    List<Reserva> findByGestor(Gestor gestor);
}
