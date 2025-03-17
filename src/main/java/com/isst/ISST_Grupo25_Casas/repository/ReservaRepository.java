package com.isst.ISST_Grupo25_Casas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.isst.ISST_Grupo25_Casas.models.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
}