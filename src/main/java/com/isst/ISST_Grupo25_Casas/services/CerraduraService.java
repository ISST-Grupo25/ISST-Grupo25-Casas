package com.isst.ISST_Grupo25_Casas.services;

import com.isst.ISST_Grupo25_Casas.models.Cerradura;
import com.isst.ISST_Grupo25_Casas.repository.CerraduraRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

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

    public String obtenerTokenPorCerradura(Cerradura Cerradura) {
        return Cerradura.getToken();
    }

    public Cerradura guardarCerradura(String ubicación, String token) {
        Cerradura cerradura = new Cerradura();
        cerradura.setUbicacion(ubicación);
        cerradura.setToken(token);
        cerradura.setBateria(100);  //Se implementará en el siguiente sprint el monitoreo de la batería
        return cerraduraRepository.save(cerradura);
    }

    public Cerradura obtenerPrimera() {
    List<Cerradura> cerraduras = obtenerTodasLasCerraduras();
    if (!cerraduras.isEmpty()) {
        return cerraduras.get(0);
    }
    throw new NoSuchElementException("No hay cerraduras disponibles.");


}
}
