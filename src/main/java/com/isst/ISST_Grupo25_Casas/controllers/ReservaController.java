package com.isst.ISST_Grupo25_Casas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.sql.Date;
import java.util.List;
import com.isst.ISST_Grupo25_Casas.models.Reserva;
import com.isst.ISST_Grupo25_Casas.repository.ReservaRepository;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    // 📌 Mostrar formulario de reservas
    @GetMapping("/nueva")
    public String mostrarFormulario(Model model) {
        model.addAttribute("reserva", new Reserva());
        return "formulario_reserva"; // Esto renderiza "formulario_reserva.html"
    }

    // 📌 Guardar la nueva reserva cuando se envíe el formulario
    @PostMapping("/guardar")
    public String guardarReserva(@ModelAttribute Reserva reserva) {
        reservaRepository.save(reserva);
        return "redirect:/reservas"; // Redirige a la lista de reservas
    }

    // 📌 Mostrar lista de reservas
    @GetMapping
    public String mostrarReservas(Model model) {
        List<Reserva> reservas = reservaRepository.findAll();
        model.addAttribute("reservas", reservas);
        return "reservas"; // Renderiza "reservas.html"
    }
}

