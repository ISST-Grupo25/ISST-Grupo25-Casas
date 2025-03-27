package com.isst.ISST_Grupo25_Casas.controllers;

import com.isst.ISST_Grupo25_Casas.models.Reserva;
import com.isst.ISST_Grupo25_Casas.models.Huesped;
import com.isst.ISST_Grupo25_Casas.services.ReservaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping("/home-access")
    public String mostrarReservas(Model model, HttpSession session) {
        Object obj = session.getAttribute("usuario");
        
        if (obj instanceof Huesped huesped) { // Validar y castear correctamente
            System.out.println("📌 Usuario logueado: " + huesped.getName());
            List<Reserva> reservas = reservaService.obtenerReservasPorHuesped(huesped.getId());
            model.addAttribute("reservas", reservas);
        } else {
            System.out.println("❌ Error: No hay un huésped en sesión");
            model.addAttribute("reservas", null);
        }
        return "home-access";
    }
}
