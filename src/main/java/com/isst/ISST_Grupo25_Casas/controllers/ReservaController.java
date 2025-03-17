package com.isst.ISST_Grupo25_Casas.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import com.isst.ISST_Grupo25_Casas.repository.ReservaRepository;
import com.isst.ISST_Grupo25_Casas.models.Reserva;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @GetMapping
    public List<Map<String, Object>> getReservas() {
        List<Reserva> reservas = reservaRepository.findAll();
        List<Map<String, Object>> eventos = new ArrayList<>();

        for (Reserva reserva : reservas) {
            Map<String, Object> evento = new HashMap<>();
            evento.put("title", "Reserva: " + reserva.getName());
            evento.put("start", reserva.getFechainicio().toString());
            evento.put("end", reserva.getFechafin().toString());
            evento.put("description", "PIN: " + reserva.getPin());

            // Asigna un color según el tipo de evento (aquí todas son reservas)
            evento.put("color", "#4CAF50"); // Verde

            eventos.add(evento);
        }
        return eventos;
    }
}

