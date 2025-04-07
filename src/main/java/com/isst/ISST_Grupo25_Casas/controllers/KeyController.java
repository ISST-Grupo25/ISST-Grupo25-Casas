package com.isst.ISST_Grupo25_Casas.controllers;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.isst.ISST_Grupo25_Casas.models.Gestor;
import com.isst.ISST_Grupo25_Casas.models.Huesped;
import com.isst.ISST_Grupo25_Casas.models.Reserva;
import com.isst.ISST_Grupo25_Casas.repository.GestorRepository;
import com.isst.ISST_Grupo25_Casas.repository.HuespedRepository;
import com.isst.ISST_Grupo25_Casas.repository.ReservaRepository;
import com.isst.ISST_Grupo25_Casas.services.AccesoService;
import com.isst.ISST_Grupo25_Casas.services.CerraduraService;
import com.isst.ISST_Grupo25_Casas.services.GestorService;
import com.isst.ISST_Grupo25_Casas.services.HuespedService;

import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.Optional;

@Controller
public class KeyController {

    private final HuespedRepository huespedRepository;
    private final GestorRepository gestorRepository;

    public KeyController(HuespedRepository huespedRepository, GestorRepository gestorRepository, AccesoService accesoService) {
        this.accesoService = accesoService;
        this.huespedRepository = huespedRepository;
        this.gestorRepository = gestorRepository;
    }

    @Autowired
   private final AccesoService accesoService;

    @GetMapping("/cerradura")
    public String home(Model model, HttpSession session) {
        model.addAttribute("title", "Inicio");
        model.addAttribute("user", session.getAttribute("user"));
        return "cerradura";
    }

    @PostMapping("/acceso/guardar")
   public String guardarAcceso(@RequestParam("horario") Date horario,
                               @RequestParam("resultado") Boolean resultado,
                                @RequestParam("reserva") Reserva reserva,
                               HttpSession session) {
        try {
            // Obtener el gestor actual desde la sesión
            Object obj = session.getAttribute("usuario");
            if (obj instanceof Huesped huesped) {
                // Llamar al servicio para guardar la cerradura con el gestor asociado
                accesoService.guardarAcceso(horario, resultado, huesped, reserva);
                return "redirect:/calendar"; // Redirigir al calendario
            } else {
                System.out.println("❌ Error: No hay un gestor en sesión");
                return "redirect:/key-access?error=sinHuesped"; // Mostrar error en la vista
            }
        } catch (Exception e) {
            System.out.println("❌ Error al guardar cerradura: " + e.getMessage());
            return "redirect:/key-access?error"; // Mostrar error en la vista
        }
    }

}
