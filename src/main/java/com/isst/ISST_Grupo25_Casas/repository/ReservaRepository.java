// ReservaRepository.java -----------------------------------------------------------
package com.isst.ISST_Grupo25_Casas.repository;

import com.isst.ISST_Grupo25_Casas.models.Gestor;
import com.isst.ISST_Grupo25_Casas.models.Huesped;
import com.isst.ISST_Grupo25_Casas.models.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;


public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByHuespedesId(Long huespedId);
    List<Reserva> findByCerraduraId(Long cerraduraId);
    List<Reserva> findByGestorId(Long gestorId);
    void deleteByCerraduraId(Long cerraduraId);
    @Query("SELECT r FROM Reserva r WHERE :huesped MEMBER OF r.huespedes AND r.fechafin >= CURRENT_DATE")
    List<Reserva> findReservasActivasOFuturasPorHuesped(@Param("huesped") Huesped huesped);
    List<Reserva> findByHuespedesContainingAndFechafinBefore(@Param("huesped") Huesped huesped, Date fecha);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM huesped_reserva WHERE reserva_id = :reservaId", nativeQuery = true)
    void eliminarHuespedesDeReserva(@Param("reservaId") Long reservaId);
    
}
