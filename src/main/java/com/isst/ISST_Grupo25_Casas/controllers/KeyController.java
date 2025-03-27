package com.isst.ISST_Grupo25_Casas.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.isst.ISST_Grupo25_Casas.models.Huesped;
import com.isst.ISST_Grupo25_Casas.repository.GestorRepository;
import com.isst.ISST_Grupo25_Casas.repository.HuespedRepository;
import com.isst.ISST_Grupo25_Casas.services.GestorService;
import com.isst.ISST_Grupo25_Casas.services.HuespedService;

import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class KeyController {

    private final HuespedRepository huespedRepository;
    private final GestorRepository gestorRepository;

    public KeyController(HuespedRepository huespedRepository, GestorRepository gestorRepository) {
        this.huespedRepository = huespedRepository;
        this.gestorRepository = gestorRepository;
    }

    @GetMapping("/cerradura")
    public String home(Model model, HttpSession session) {
        model.addAttribute("title", "Inicio");
        model.addAttribute("user", session.getAttribute("user"));
        return "cerradura";
    }

}
