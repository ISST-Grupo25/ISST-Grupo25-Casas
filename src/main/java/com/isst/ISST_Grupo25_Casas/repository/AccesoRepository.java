package com.isst.ISST_Grupo25_Casas.repository;

import com.isst.ISST_Grupo25_Casas.models.Acceso;
import com.isst.ISST_Grupo25_Casas.models.Huesped;
import com.isst.ISST_Grupo25_Casas.models.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface AccesoRepository extends JpaRepository<Acceso, Long> {

    // 🔵 Buscar accesos por un huesped (por objeto)
    List<Acceso> findByHuesped(Huesped huesped);

    // 🔵 Buscar accesos por reserva (por objeto)
    List<Acceso> findAllByReserva(Reserva reserva); // ✅ Devuelve todos los accesos

    List<Acceso> findAllByReservaId(Long reservaId);


    List<Acceso> findByReservaIdIn(List<Long> reservaIds);

    List<Acceso> findAllByReservaIdInAndLeidoFalse(List<Long> reservaIds);

    @Query("""
      select count(a) 
      from Acceso a 
      where a.leido = false 
      and a.reserva.gestor.id = :gestorId
    """)
    long countByGestorIdAndLeidoFalse(@Param("gestorId") Long gestorId);
}