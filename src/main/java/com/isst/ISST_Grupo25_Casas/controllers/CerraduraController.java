package com.isst.ISST_Grupo25_Casas.controllers;

import com.isst.ISST_Grupo25_Casas.models.Reserva;
import com.isst.ISST_Grupo25_Casas.repository.ReservaRepository;
import com.isst.ISST_Grupo25_Casas.models.Cerradura;
import com.isst.ISST_Grupo25_Casas.models.Gestor;
import com.isst.ISST_Grupo25_Casas.models.Huesped;
import com.isst.ISST_Grupo25_Casas.services.CerraduraService;
import com.isst.ISST_Grupo25_Casas.services.HuespedService;
import com.isst.ISST_Grupo25_Casas.services.ReservaService;
import com.isst.ISST_Grupo25_Casas.services.GestorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CerraduraController {

    @Autowired
    private final CerraduraService cerraduraService;
    @Autowired
    private ReservaRepository reservaRepository; // Acceso al repositorio de reservas

    private final RestTemplate restTemplate = new RestTemplate();

    public CerraduraController(CerraduraService cerraduraService) {
        this.cerraduraService = cerraduraService;
        this.reservaRepository = reservaRepository;
        
    }
    
    @PostMapping("/cerradura/guardar")
    public String guardarCerradura(@RequestParam("ubicacion") String ubicacion,
                                @RequestParam("token") String token) {


        try {
            cerraduraService.guardarCerradura(ubicacion, token);
            return "redirect:/calendar"; // Redirigir al calendario


        } catch (Exception e) {
            System.out.println("❌ Error al guardar reserva: " + e.getMessage());
            return "redirect:/calendar?error"; // Mostrar error en la vista
        }
    }

    @PostMapping("/cerradura/abrir")
    public String abrirCerradura(@RequestParam("pin") String pin,
                                 @RequestParam("reservaId") Long reservaId,
                                 Model model) {
        try {
            // Verificar el PIN
            if (esPinValido(pin, reservaId)) {
                // Generar un token
                String token = cerraduraService.obtenerTokenPorCerradura(null);

                // Enviar el token a Backend 2 para abrir la cerradura
                String backend2Url = "http://localhost:3555/abrirCerradura"; // URL de Backend 2
                ResponseEntity<String> response = restTemplate.postForEntity(backend2Url, token, String.class);

                // Verificar la respuesta de Backend 2
                if (response.getStatusCode().is2xxSuccessful() && "abierta".equals(response.getBody())) {
                    model.addAttribute("message", "Puerta Abierta");
                } else {
                    model.addAttribute("message", "Error al abrir la puerta");
                }
                return "redirect:/calendar"; // Redirigir al calendario

            } else {
                model.addAttribute("message", "PIN Inválido");
                return "redirect:/calendar?error"; // Mostrar error en la vista
            }
        } catch (Exception e) {
            System.out.println("❌ Error al abrir la cerradura: " + e.getMessage());
            return "redirect:/calendar?error"; // Mostrar error en la vista
        }
    }

    private boolean esPinValido(String pin, Long reservaId) {
        // Buscar la reserva por su ID
        Reserva reserva = reservaRepository.findById(reservaId).orElse(null);

        // Verificar si la reserva existe y si el PIN coincide
        if (reserva != null && reserva.getPin().equals(pin)) {
            return true; // El PIN es válido para esta reserva
        }
        return false; // PIN inválido
    }

}
