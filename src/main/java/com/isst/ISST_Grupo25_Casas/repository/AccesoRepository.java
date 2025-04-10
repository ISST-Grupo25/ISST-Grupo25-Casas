package com.isst.ISST_Grupo25_Casas.repository;

import com.isst.ISST_Grupo25_Casas.models.Acceso;
import com.isst.ISST_Grupo25_Casas.models.Huesped;
import com.isst.ISST_Grupo25_Casas.models.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface AccesoRepository extends JpaRepository<Acceso, Long> {

    // 🔵 Buscar accesos por un huesped (por objeto)
    List<Acceso> findByHuesped(Huesped huesped);

    // 🔵 Buscar accesos por reserva (por objeto)
    List<Acceso> findByReserva(Reserva reserva);
}
