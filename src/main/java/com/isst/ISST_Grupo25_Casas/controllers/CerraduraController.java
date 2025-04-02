package com.isst.ISST_Grupo25_Casas.controllers;

import com.isst.ISST_Grupo25_Casas.models.Reserva;
import com.isst.ISST_Grupo25_Casas.repository.ReservaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isst.ISST_Grupo25_Casas.models.Cerradura;
import com.isst.ISST_Grupo25_Casas.models.Gestor;
import com.isst.ISST_Grupo25_Casas.models.Huesped;
import com.isst.ISST_Grupo25_Casas.services.CerraduraService;
import com.isst.ISST_Grupo25_Casas.services.HuespedService;
import com.isst.ISST_Grupo25_Casas.services.ReservaService;
import com.isst.ISST_Grupo25_Casas.services.GestorService;
import jakarta.servlet.http.HttpSession;

import org.checkerframework.checker.units.qual.s;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
    @ResponseBody
    public String guardarCerraduraAjax(@RequestParam("ubicacion") String ubicacion,
                                    @RequestParam("token") String token) {
        try {
            Cerradura nueva = cerraduraService.guardarCerradura(ubicacion, token);
            return String.valueOf(nueva.getId()); // ✅ Devuelve el ID al JS
        } catch (Exception e) {
            System.out.println("❌ Error al guardar cerradura: " + e.getMessage());
            return "-1"; // Puedes personalizarlo más si quieres
        }
    }
}
