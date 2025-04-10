package com.isst.ISST_Grupo25_Casas.repository;

import com.isst.ISST_Grupo25_Casas.models.Cerradura;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
    
public interface CerraduraRepository extends JpaRepository<Cerradura, Long> {
    List<Cerradura> findByGestorId(Long gestorId);
}