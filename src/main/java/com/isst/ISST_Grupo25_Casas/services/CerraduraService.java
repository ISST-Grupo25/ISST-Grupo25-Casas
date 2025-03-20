package com.isst.ISST_Grupo25_Casas.services;

import com.isst.ISST_Grupo25_Casas.models.Cerradura;
import com.isst.ISST_Grupo25_Casas.repository.CerraduraRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CerraduraService {

    private final CerraduraRepository cerraduraRepository;

    public CerraduraService(CerraduraRepository cerraduraRepository) {
        this.cerraduraRepository = cerraduraRepository;
    }

    public List<Cerradura> obtenerTodasLasCerraduras() {
        return cerraduraRepository.findAll();
    }

    public Cerradura obtenerCerraduraPorId(Long id) {
        return cerraduraRepository.findById(id).orElse(null);
    }

    public Cerradura guardarCerradura(String ubicación, String token) {
        Cerradura cerradura = new Cerradura();
        cerradura.setUbicacion(ubicación);
        cerradura.setToken(token);
        return cerraduraRepository.save(cerradura);
    }
}
