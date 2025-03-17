package com.isst.ISST_Grupo25_Casas.controllers;

import com.isst.ISST_Grupo25_Casas.repository.GestorRepository;
import com.isst.ISST_Grupo25_Casas.repository.HuespedRepository;
import com.isst.ISST_Grupo25_Casas.models.Huesped;
import com.isst.ISST_Grupo25_Casas.services.HuespedService;
import com.isst.ISST_Grupo25_Casas.services.HuespedService;
import com.isst.ISST_Grupo25_Casas.models.Gestor;
import com.isst.ISST_Grupo25_Casas.services.GestorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
        System.out.println("➡️ Intentando autenticar: " + email); // 🔹 Comprobación

        Optional<Huesped> huesped = huespedService.authenticate(email, password);
        Optional<Gestor> gestor = gestorService.authenticate(email, password);

        if (huesped.isPresent()) {
            session.setAttribute("usuario", huesped.get().getName());
            session.setAttribute("email", huesped.get().getEmail());
            session.setAttribute("isLoggedIn", true);

            System.out.println("✅ Huesped autenticado: " + huesped.get().getName());
            System.out.println("📌 Sesión almacenada: " + session.getAttribute("usuario"));

            return "redirect:/";
        } else if (gestor.isPresent()) {
            session.setAttribute("usuario", gestor.get().getName());
            session.setAttribute("email", gestor.get().getEmail());
            session.setAttribute("isLoggedIn", true);

            System.out.println("✅ Gestor autenticado: " + gestor.get().getName());
            System.out.println("📌 Sesión almacenada: " + session.getAttribute("usuario"));

            return "redirect:/";
        } else {
            System.out.println("❌ Fallo en la autenticación para: " + email);
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
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String nombre, 
                        @RequestParam String email, 
                        @RequestParam String password, 
                        @RequestParam String role, 
                        @RequestParam(required = false) String phone,
                        HttpSession session) {
        try {
            if ("gestor".equalsIgnoreCase(role)) {

                // Validar si el teléfono es null o vacío antes de pasarlo
                if (phone == null || phone.trim().isEmpty()) {
                    return "redirect:/signup?error=El teléfono es obligatorio para los gestores";
                }

                Gestor gestor = gestorService.registerGestor(nombre, email, password, phone);

                session.setAttribute("userId", gestor.getId());
                session.setAttribute("userType", "gestor");
                session.setAttribute("usuario", gestor.getName());
                session.setAttribute("email", gestor.getEmail());

                System.out.println("✅ Gestor registrado: " + gestor.getName());

                // 🔐 Autenticar gestor automáticamente
                Optional<Gestor> authenticatedGestor = gestorService.authenticate(email, password);
                if (authenticatedGestor.isPresent()) {
                    session.setAttribute("usuario", authenticatedGestor.get().getName());
                    session.setAttribute("email", authenticatedGestor.get().getEmail());
                    session.setAttribute("isLoggedIn", true);
                    session.setAttribute("userType", "gestor");

                    System.out.println("✅ Gestor autenticado automáticamente: " + authenticatedGestor.get().getName());
                }

            } else if ("huesped".equalsIgnoreCase(role)) {

                Huesped huesped = huespedService.registerHuesped(nombre, email, password);

                System.out.println("✅ Huesped registrado: " + huesped.getName());

                session.setAttribute("userId", huesped.getId());
                session.setAttribute("userType", "huesped");
                session.setAttribute("usuario", huesped.getName());
                session.setAttribute("email", huesped.getEmail());

                // 🔐 Autenticar huésped automáticamente
                Optional<Huesped> authenticatedHuesped = huespedService.authenticate(email, password);
                if (authenticatedHuesped.isPresent()) {
                    session.setAttribute("usuario", authenticatedHuesped.get().getName());
                    session.setAttribute("email", authenticatedHuesped.get().getEmail());
                    session.setAttribute("isLoggedIn", true);
                    session.setAttribute("userType", "huesped");

                    System.out.println("✅ Huesped autenticado automáticamente: " + authenticatedHuesped.get().getName());
                }
            } else {
                return "redirect:/signup?error=Tipo de usuario inválido";
            }

            return "redirect:/profile";
        } catch (Exception e) {
            return "redirect:/signup?error=" + e.getMessage();
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
