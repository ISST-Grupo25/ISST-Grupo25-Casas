package com.isst.ISST_Grupo25_Casas.controllers;

import com.isst.ISST_Grupo25_Casas.models.Reserva;
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

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CerraduraController {

    @Autowired
    private final CerraduraService cerraduraService;

    public CerraduraController(CerraduraService cerraduraService) {
        this.cerraduraService = cerraduraService;
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
