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
public class HomeController {

    private final HuespedRepository huespedRepository;
    private final GestorRepository gestorRepository;

    public HomeController(HuespedRepository huespedRepository, GestorRepository gestorRepository) {
        this.huespedRepository = huespedRepository;
        this.gestorRepository = gestorRepository;
    }

    @GetMapping("/index")
    public String home(Model model, HttpSession session) {
        model.addAttribute("title", "Inicio");
        model.addAttribute("user", session.getAttribute("user"));
        return "index";
    }

    @GetMapping("/contact") 
    public String contact(Model model, HttpSession session) {
        model.addAttribute("user", session.getAttribute("user"));
        return "contact"; // Make sure "contact.html" exists
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        String user = (String) session.getAttribute("user");
        String email = (String) session.getAttribute("email");

        model.addAttribute("user", user);
        model.addAttribute("email", email);
        return "profile";
    }

    @GetMapping("/settings") 
    public String settings(Model model, HttpSession session) {
        model.addAttribute("user", session.getAttribute("user"));
        return "settings";
    }

}
