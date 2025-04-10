// ReservaRepository.java -----------------------------------------------------------
package com.isst.ISST_Grupo25_Casas.repository;

import com.isst.ISST_Grupo25_Casas.models.Gestor;
import com.isst.ISST_Grupo25_Casas.models.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByHuespedesId(Long huespedId);
    List<Reserva> findByCerraduraId(Long cerraduraId);
    List<Reserva> findByGestor(Gestor gestor);
    void deleteByCerraduraId(Long cerraduraId);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM huesped_reserva WHERE reserva_id = :reservaId", nativeQuery = true)
    void eliminarHuespedesDeReserva(@Param("reservaId") Long reservaId);
}
