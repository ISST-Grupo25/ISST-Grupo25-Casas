package com.isst.ISST_Grupo25_Casas.controllers;

import com.isst.ISST_Grupo25_Casas.models.Huesped;
import com.isst.ISST_Grupo25_Casas.repository.HuespedRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/huespedes")
@CrossOrigin(origins = "*") // Permitir peticiones desde el frontend
public class HuespedController {

    private final HuespedRepository huespedRepository;

    public HuespedController(HuespedRepository huespedRepository) {
        this.huespedRepository = huespedRepository;
    }

    @GetMapping
    public List<Huesped> getAllHuespedes() {
        return huespedRepository.findAll();
    }
}
