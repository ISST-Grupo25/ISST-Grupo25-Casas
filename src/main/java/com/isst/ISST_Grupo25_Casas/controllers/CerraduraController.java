package com.isst.ISST_Grupo25_Casas.controllers;

import com.isst.ISST_Grupo25_Casas.models.Cerradura;
import com.isst.ISST_Grupo25_Casas.repository.CerraduraRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cerraduras")
@CrossOrigin(origins = "*") // Permitir peticiones desde el frontend
public class CerraduraController {

    private final CerraduraRepository cerraduraRepository;

    public CerraduraController(CerraduraRepository cerraduraRepository) {
        this.cerraduraRepository = cerraduraRepository;
    }

    @GetMapping
    public List<Cerradura> getAllCerraduras() {
        return cerraduraRepository.findAll();
    }
}
