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
        model.addAttribute("usuario", session.getAttribute("usuario"));
        return "index";
    }

    @GetMapping("/contact") 
    public String contact(Model model, HttpSession session) {
        Object obj = session.getAttribute("usuario");

        model.addAttribute("usuario", session.getAttribute("usuario"));
        return "contact"; // Make sure "contact.html" exists
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {  // Si no hay usuario en la sesión
            return "redirect:/login";  // Redirigir a la página de login
        }
        return "profile";
    }

    @GetMapping("/settings") 
    public String settings(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {  // Si no hay usuario en la sesión
            return "redirect:/login";  // Redirigir a la página de login
        }
        System.out.println("➡️ Usuario en el perfil: " + session.getAttribute("usuario"));

        model.addAttribute("usuario", session.getAttribute("usuario"));
        return "settings";
    }

    @GetMapping("/monitor") 
    public String monitor(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {  
            System.out.println("❌ No hay usuario en la sesión");
            return "redirect:/login";  // Redirigir a la página de login
        }
        model.addAttribute("usuario", session.getAttribute("usuario"));
        return "monitor";
    }

}
