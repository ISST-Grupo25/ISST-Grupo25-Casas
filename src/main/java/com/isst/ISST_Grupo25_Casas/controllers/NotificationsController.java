package com.isst.ISST_Grupo25_Casas.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.isst.ISST_Grupo25_Casas.models.Acceso;
import com.isst.ISST_Grupo25_Casas.models.Gestor;
import com.isst.ISST_Grupo25_Casas.models.Huesped;
import com.isst.ISST_Grupo25_Casas.models.Reserva;
import com.isst.ISST_Grupo25_Casas.services.AccesoService;
import com.isst.ISST_Grupo25_Casas.services.ReservaService;

import jakarta.servlet.http.HttpSession;


@Controller
public class NotificationsController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private AccesoService accesoService;

    @GetMapping("/notifications")
    public String notifications(Model model, HttpSession session) {

        Object usuario = session.getAttribute("usuario");
        if (usuario == null) {
            System.out.println("❌ No hay un usuario en la sesión");
            return "redirect:/login"; // Redirigir a login si no está logueado
        }

        List<Reserva> reservas;
        if (usuario instanceof Gestor gestor) {
            // Obtener reservas asociadas al gestor
            reservas = reservaService.obtenerReservasPorGestor(gestor.getId());
            model.addAttribute("usuario", gestor);
        } else {
            System.out.println("❌ Usuario no autorizado");
            return "redirect:/login"; // Redirigir si el usuario no es válido
        }


        List<Acceso> accesos = reservas.stream()
            .flatMap(reserva -> reserva.getAccesos().stream())
            .toList();

        model.addAttribute("accesos", accesos);
        System.out.println("🔵 Accesos: " + accesos);

        return "notifications";
    }

}
