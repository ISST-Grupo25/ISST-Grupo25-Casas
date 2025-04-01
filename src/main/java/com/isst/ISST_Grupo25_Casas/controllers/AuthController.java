
package com.isst.ISST_Grupo25_Casas.controllers;

import com.isst.ISST_Grupo25_Casas.repository.GestorRepository;
import com.isst.ISST_Grupo25_Casas.repository.HuespedRepository;
import com.isst.ISST_Grupo25_Casas.models.Huesped;
import com.isst.ISST_Grupo25_Casas.services.HuespedService;
import com.isst.ISST_Grupo25_Casas.models.Gestor;
import com.isst.ISST_Grupo25_Casas.services.GestorService;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

@Controller
public class AuthController {

    private final HuespedService huespedService;
    private final GestorService gestorService;

    public AuthController(HuespedService huespedService, GestorService gestorService) {
        this.gestorService = gestorService;
        this.huespedService = huespedService;
    }

    @PostMapping("/perform_login")
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session) {
        System.out.println("➡️ Intentando autenticar: " + email);

        Optional<Huesped> huesped = huespedService.authenticate(email, password);
        Optional<Gestor> gestor = gestorService.authenticate(email, password);

        if (huesped.isPresent()) {
            session.setAttribute("usuario", huesped.get()); // Almacenar objeto completo
            session.setAttribute("role", "huesped");
            session.setAttribute("isLoggedIn", true);
            return "redirect:/home-access";
        } else if (gestor.isPresent()) {
            session.setAttribute("usuario", gestor.get()); // Almacenar objeto completo
            session.setAttribute("role", "gestor");
            session.setAttribute("isLoggedIn", true);
            return "redirect:/calendar";
        } else {
            return "redirect:/login?error=true";
        }
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos.");
        }
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String nombre, 
                         @RequestParam String email, 
                         @RequestParam String password, 
                         @RequestParam String role, 
                         @RequestParam(required = false) String phone, RedirectAttributes redirectAttributes,
                         HttpSession session, Model model) {
        try {
            if ("gestor".equalsIgnoreCase(role)) {
                if (phone == null || phone.trim().isEmpty()) {
                    return "redirect:/signup?error=El teléfono es obligatorio para los gestores";
                }
                Gestor gestor = gestorService.registerGestor(nombre, email, password, phone);

                Optional<Gestor> gestorToLogin = gestorService.authenticate(email, password);
        
                if (gestorToLogin.isPresent()) {
                    session.setAttribute("usuario", gestorToLogin.get()); // Almacenar objeto completo
                    session.setAttribute("role", "gestor");
                    session.setAttribute("isLoggedIn", true);
                    return "redirect:/calendar";
                } else {
                    return "redirect:/login?error=true";
                }
                
            } else if ("huesped".equalsIgnoreCase(role)) {
                Huesped huesped = huespedService.registerHuesped(nombre, email, password);

                Optional<Huesped> huespedToLogin = huespedService.authenticate(email, password);

                if (huespedToLogin.isPresent()) {
                    session.setAttribute("usuario", huespedToLogin.get()); // Almacenar objeto completo
                    session.setAttribute("role", "huesped");
                    session.setAttribute("isLoggedIn", true);
                    return "redirect:/home-access";
                } else {
                    return "redirect:/login?error=true";
                }
            } else {
                return "redirect:/signup?error=Tipo de usuario inválido";
            }
            
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/signup?error=" + e.getMessage();
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/oauth-success")
    public String oauthSuccess(Authentication authentication, HttpSession session) {
        OAuth2AuthenticationToken oauth = (OAuth2AuthenticationToken) authentication;
        String email = oauth.getPrincipal().getAttribute("email");
        String nombre = oauth.getPrincipal().getAttribute("name");

        System.out.println("✅ Login con Google: " + email + " (" + nombre + ")");

        // Si ya está en tu BD como gestor o huesped, lo recuperas
        Optional<Gestor> gestor = gestorService.findByEmail(email);
        Optional<Huesped> huesped = huespedService.findByEmail(email);

        if (gestor.isPresent()) {
            session.setAttribute("usuario", gestor.get());
            session.setAttribute("role", "gestor");
        } else if (huesped.isPresent()) {
            session.setAttribute("usuario", huesped.get());
            session.setAttribute("role", "huesped");
        } else {
            // Si no existe, lo registras como huésped por defecto (puedes cambiar esto)
            Huesped nuevo = huespedService.registerHuesped(nombre, email, UUID.randomUUID().toString());
            session.setAttribute("usuario", nuevo);
            session.setAttribute("role", "huesped");
        }

        session.setAttribute("isLoggedIn", true);
        return "redirect:/";
    }

}