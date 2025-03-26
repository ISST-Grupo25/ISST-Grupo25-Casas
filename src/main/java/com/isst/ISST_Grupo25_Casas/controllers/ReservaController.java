package com.isst.ISST_Grupo25_Casas.controllers;

import com.isst.ISST_Grupo25_Casas.models.Reserva;
import com.google.api.client.util.DateTime;
import com.isst.ISST_Grupo25_Casas.models.Cerradura;
import com.isst.ISST_Grupo25_Casas.models.Gestor;
import com.isst.ISST_Grupo25_Casas.models.Huesped;
import com.isst.ISST_Grupo25_Casas.services.CerraduraService;
import com.isst.ISST_Grupo25_Casas.services.HuespedService;
import com.isst.ISST_Grupo25_Casas.services.ReservaService;
import com.isst.ISST_Grupo25_Casas.services.GestorService;

import com.isst.ISST_Grupo25_Casas.services.GoogleCalendarService;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.client.util.DateTime;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Controller
public class ReservaController {

    private final ReservaService reservaService;
    private final CerraduraService cerraduraService;
    private final HuespedService huespedService;
    

    public ReservaController(ReservaService reservaService, CerraduraService cerraduraService, HuespedService huespedService) {
        this.reservaService = reservaService;
        this.cerraduraService = cerraduraService;
        this.huespedService = huespedService;

    }

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

    @GetMapping("/calendar")
    public String mostrarFormularioReserva(Model model) {

            List<Reserva> reservas = reservaService.obtenerTodasLasReservas();
            List<Cerradura> cerraduras = cerraduraService.obtenerTodasLasCerraduras();
            List<Huesped> huespedes = huespedService.obtenerTodosLosHuespedes();

            if (cerraduras.isEmpty()) {
                System.out.println("⚠️ No hay cerraduras registradas en la BD");
            }
            if (huespedes.isEmpty()) {
                System.out.println("⚠️ No hay huéspedes registrados en la BD");
            }

            model.addAttribute("reservas", reservas);
            model.addAttribute("cerraduras", cerraduras);
            model.addAttribute("huespedes", huespedes);
            
            return "calendar"; // Carga la vista "calendar.html"
    }

    @PostMapping("/calendar/guardar")
    public String guardarReserva(@RequestParam("casa") Long cerraduraId,
                                @RequestParam("fechaInicio") String fechaInicioStr,
                                @RequestParam("fechaFin") String fechaFinStr,
                                @RequestParam(value = "huespedes", required = false) List<Long> huespedIds,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {


        try {
            Object obj = session.getAttribute("usuario");
         if (obj instanceof Gestor gestor) {  // Validar y castear correctamente el usuario
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilFechaInicio = sdf.parse(fechaInicioStr);
            java.util.Date utilFechaFin = sdf.parse(fechaFinStr);

            Date fechaInicio = new Date(utilFechaInicio.getTime());
            Date fechaFin = new Date(utilFechaFin.getTime());

            Cerradura cerradura = cerraduraService.obtenerCerraduraPorId(cerraduraId);
            List<Huesped> huespedes = (huespedIds != null) ? huespedService.obtenerHuespedesPorIds(huespedIds) : new ArrayList<>();


            //  Verificar si ya existe una reserva en ese rango para esa casa
            boolean existe = reservaService.existeReservaEnEseRangoYCasa(fechaInicio, fechaFin, cerradura);
            if (existe) {
                redirectAttributes.addFlashAttribute("errorReserva", "Ya existe una reserva en esas fechas para esta casa.");
                return "redirect:/calendar";
            }

            reservaService.guardarReserva(fechaInicio, fechaFin, cerradura, huespedes, gestor);


            //Ahora intentamos guardar en google calendar
            String resumen = "Reserva para casa: " + cerradura.getUbicacion();
            String descripcion = "Reserva gestionada desde la plataforma IoH. Huéspedes: " + huespedes.size();

            String inicioStr = fechaInicioStr + "T12:00:00+02:00";  // Ajusta según necesidad
            String finStr = fechaFinStr + "T14:00:00+02:00";


            //ESTARÍA MUY BIEN QUE TUVIESEN EL MISMO ID
            try {
                GoogleCalendarService.createEvent(resumen, descripcion, inicioStr, finStr);
                System.out.println("✅ Evento creado en Google Calendar");
            } catch (Exception ex) {
                System.out.println("⚠️ No se pudo crear el evento en Google Calendar: " + ex.getMessage());
            }

            return "redirect:/calendar"; // Redirigir al calendario
         }else {
            return "redirect:/calendar?error"; // Redirigir al login si no hay un usuario en sesión
         }

        } catch (Exception e) {
            System.out.println("❌ Error al guardar reserva: " + e.getMessage());
            return "redirect:/calendar?error"; // Mostrar error en la vista
        }
    }




    @PostMapping("/calendar/importar")
    public String importarDesdeGoogle(HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            Object obj = session.getAttribute("usuario");
            if (obj instanceof Gestor gestor) {
                Cerradura cerradura = cerraduraService.obtenerPrimera();
                Huesped huesped = huespedService.obtenerPrimero();

                int importadas = reservaService.importarDesdeGoogle(cerradura, huesped, gestor);
                redirectAttributes.addFlashAttribute("importadas", importadas);
                return "redirect:/calendar";
            } else {
                return "redirect:/calendar?error";
            }
        } catch (Exception e) {
            return "redirect:/calendar?errorGoogle";
        }
    }

    @GetMapping("/reservas")
@ResponseBody
public List<Map<String, Object>> obtenerReservasParaCalendario() {
    List<Reserva> reservas = reservaService.obtenerTodasLasReservas();

    List<Map<String, Object>> eventos = new ArrayList<>();

    for (Reserva r : reservas) {
        Map<String, Object> evento = new HashMap<>();
        evento.put("title", "Reserva casa " + r.getCerradura().getUbicacion());
        evento.put("start", r.getFechainicio().toString());
        evento.put("end", r.getFechafin().toString());
        eventos.add(evento);
    }

    return eventos;
}

}

