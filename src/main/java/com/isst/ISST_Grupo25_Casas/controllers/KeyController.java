package com.isst.ISST_Grupo25_Casas.controllers;

import jakarta.servlet.http.HttpSession;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class KeyController {

    private final HuespedRepository huespedRepository;
    private final GestorRepository gestorRepository;

    @Autowired
   private final AccesoService accesoService;

    @Autowired
    private final ReservaRepository reservaRepository;

    public KeyController(HuespedRepository huespedRepository, GestorRepository gestorRepository, AccesoService accesoService, ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
        this.accesoService = accesoService;
        this.huespedRepository = huespedRepository;
        this.gestorRepository = gestorRepository;
    }

    

    @GetMapping("/cerradura")
    public String home(Model model, HttpSession session) {
        model.addAttribute("title", "Inicio");
        model.addAttribute("user", session.getAttribute("user"));
        return "cerradura";
    }

    @PostMapping("/acceso/guardar")
    public ResponseEntity<?> guardarAcceso(
        @RequestParam("horario") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime horario,
        @RequestParam("resultado") Boolean resultado,
        @RequestParam("reservaId") Long reservaId,
        HttpSession session
    ) {
        System.out.println("Horario recibido: " + horario);
        System.out.println("Resultado recibido: " + resultado);
        System.out.println("Reserva ID recibido: " + reservaId);

        Object obj = session.getAttribute("usuario");
        System.out.println("Usuario en la sesión: " + obj);
        if (obj instanceof Huesped huesped) {
            Optional<Reserva> optionalReserva = reservaRepository.findById(reservaId);
            if (optionalReserva.isEmpty()) {
                return ResponseEntity.badRequest().body("Reserva no encontrada");
            }
            Reserva reserva = optionalReserva.get();
    
            java.sql.Date sqlDate = java.sql.Date.valueOf(horario.toLocalDate());
            System.out.println("Acceso guardado: " + sqlDate + ", " + resultado + ", " + huesped.getId() + ", " + reserva.getId());            accesoService.guardarAcceso(sqlDate, resultado, huesped, reserva);
            return ResponseEntity.ok("Acceso registrado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body("Usuario no autorizado");
        }
    }

}
