package com.isst.ISST_Grupo25_Casas.controllers;

import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.isst.ISST_Grupo25_Casas.models.Acceso;
import com.isst.ISST_Grupo25_Casas.models.Reserva;
import com.isst.ISST_Grupo25_Casas.services.AccesoService;
import com.isst.ISST_Grupo25_Casas.services.ReservaService;

@Controller
@RequestMapping("/monitor")
public class MonitorController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private AccesoService accesoService;

    @GetMapping
    public String monitor(Model model, HttpSession session) {
        // Comprobar que haya sesión iniciada
        if (session.getAttribute("usuario") == null) {
            System.out.println("❌ No hay usuario en la sesión");
            return "redirect:/login";  // Redirigir a login si no está logueado
        }

        // Añadir usuario actual al modelo
        model.addAttribute("usuario", session.getAttribute("usuario"));

        // Obtener reservas y accesos
        List<Reserva> reservas = reservaService.obtenerTodasLasReservas();
        List<Acceso> accesos = accesoService.obtenerTodasLasAccesos();

        // Añadir al modelo los datos
        model.addAttribute("reservas", reservas);
        model.addAttribute("totalReservas", reservas.size());
        model.addAttribute("accesosHoy", calcularAccesosHoy(accesos));

        return "monitor"; // Devuelve monitor.html
    }

    private long calcularAccesosHoy(List<Acceso> accesos) {
        // Contar los accesos del día de hoy
        return accesos.stream()
                .filter(acceso -> acceso.getHorario().toLocalDate().equals(LocalDate.now()))
                .count();
    }
}
