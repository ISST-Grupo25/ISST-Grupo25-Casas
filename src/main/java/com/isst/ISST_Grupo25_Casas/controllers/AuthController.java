package com.isst.ISST_Grupo25_Casas.controllers;

import com.isst.ISST_Grupo25_Casas.models.User;
import com.isst.ISST_Grupo25_Casas.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/perform_login")
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session) {
        System.out.println("➡️ Intentando autenticar: " + email); // 🔹 Comprobación

        Optional<User> user = userService.authenticate(email, password);
        if (user.isPresent()) {
            session.setAttribute("usuario", user.get().getName());
            session.setAttribute("email", user.get().getEmail());
            session.setAttribute("role", user.get().getRole());
            session.setAttribute("isLoggedIn", true);

            System.out.println("✅ Usuario autenticado: " + user.get().getName());
            System.out.println("📌 Sesión almacenada: " + session.getAttribute("usuario"));

            return "redirect:/";
        }

        System.out.println("❌ Fallo en la autenticación para: " + email);
        return "redirect:/login?error=true";
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
    public String signup(@RequestParam String nombre, @RequestParam String email, @RequestParam String password, @RequestParam String role, HttpSession session) {
        try {
            User user = userService.registerUser(nombre, email, password, role);
            session.setAttribute("usuario", user.getName());
            session.setAttribute("email", user.getEmail());

            return "redirect:/profile";
        } catch (RuntimeException e) {
            return "redirect:/signup?error=" + e.getMessage();
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
