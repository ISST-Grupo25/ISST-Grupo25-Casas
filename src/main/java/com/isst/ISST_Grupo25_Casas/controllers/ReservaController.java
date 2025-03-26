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

import jakarta.servlet.http.HttpServletResponse;
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

import java.io.IOException;
import java.security.GeneralSecurityException;
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
            if (!(obj instanceof Gestor gestor)) {
                System.out.println("⚠️ No hay un usuario gestor en sesión ");
                return "redirect:/calendar?error";
            }
    
            // Verificar si está autorizado con Google
            var credential = GoogleCalendarService.getFlow().loadCredential("isst");
            if (credential == null || credential.getAccessToken() == null) {
                // Guardar temporalmente los datos necesarios
                session.setAttribute("pendingAction", "guardar");
                session.setAttribute("form_cerraduraId", cerraduraId);
                session.setAttribute("form_fechaInicio", fechaInicioStr);
                session.setAttribute("form_fechaFin", fechaFinStr);
                session.setAttribute("form_huespedIds", huespedIds);
                return "redirect:/google-calendar/authorize";
            }
    
            return ejecutarGuardarReserva(gestor, cerraduraId, fechaInicioStr, fechaFinStr, huespedIds, redirectAttributes);
        } catch (Exception e) {
            System.out.println("❌ Error al guardar reserva: " + e.getMessage());
            return "redirect:/calendar?error";
        }
    }

    private String ejecutarGuardarReserva(Gestor gestor, Long cerraduraId,
                                      String fechaInicioStr, String fechaFinStr,
                                      List<Long> huespedIds,
                                      RedirectAttributes redirectAttributes) {
    try {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaInicio = new Date(sdf.parse(fechaInicioStr).getTime());
        Date fechaFin = new Date(sdf.parse(fechaFinStr).getTime());

        Cerradura cerradura = cerraduraService.obtenerCerraduraPorId(cerraduraId);
        List<Huesped> huespedes = (huespedIds != null) ? huespedService.obtenerHuespedesPorIds(huespedIds) : new ArrayList<>();

        boolean existe = reservaService.existeReservaEnEseRangoYCasa(fechaInicio, fechaFin, cerradura);
        if (existe) {
            redirectAttributes.addFlashAttribute("errorReserva", "Ya existe una reserva en esas fechas para esta casa.");
            return "redirect:/calendar";
        }

        reservaService.guardarReserva(fechaInicio, fechaFin, cerradura, huespedes, gestor);
        System.out.println("✅ Reserva creada en base de datos");

        // Crear evento en Google Calendar
        String resumen = "Reserva para casa: " + cerradura.getUbicacion();
        String descripcion = "Reserva gestionada desde IoH. Huéspedes: " + huespedes.size();
        String inicioStr = fechaInicioStr + "T12:00:00+02:00";
        String finStr = fechaFinStr + "T14:00:00+02:00";

        GoogleCalendarService.createEvent(resumen, descripcion, inicioStr, finStr);
        System.out.println("✅ Evento creado en Google Calendar");

        return "redirect:/calendar";

    } catch (Exception e) {
        System.out.println("❌ Error al guardar reserva: " + e.getMessage());
        return "redirect:/calendar?error";
    }
}










@GetMapping("/google-calendar/authorize")
public String googleCalendarAuthorize() {
    try {
        String url = GoogleCalendarService.getAuthorizationUrl("isst"); // El ID de usuario puede ser "isst"
        return "redirect:" + url;
    } catch (Exception e) {
        System.out.println("❌ Error al generar URL de autorización: " + e.getMessage());
        return "redirect:/calendar?errorGoogleAuth";
    }
}

@GetMapping("/google-calendar/callback")
public String googleCalendarCallback(@RequestParam("code") String code, HttpSession session) {
    try {
        GoogleCalendarService.autorizarConCodigo(code, "isst");

        // Recuperar acción pendiente
        String pending = (String) session.getAttribute("pendingAction");
        if ("guardar".equals(pending)) {
            Long cerraduraId = (Long) session.getAttribute("form_cerraduraId");
            String fechaInicio = (String) session.getAttribute("form_fechaInicio");
            String fechaFin = (String) session.getAttribute("form_fechaFin");
            List<Long> huespedIds = (List<Long>) session.getAttribute("form_huespedIds");
            Gestor gestor = (Gestor) session.getAttribute("usuario");

            // Borramos los atributos temporales
            session.removeAttribute("pendingAction");
            session.removeAttribute("form_cerraduraId");
            session.removeAttribute("form_fechaInicio");
            session.removeAttribute("form_fechaFin");
            session.removeAttribute("form_huespedIds");

            return ejecutarGuardarReserva(gestor, cerraduraId, fechaInicio, fechaFin, huespedIds, null);
        }

        // Puedes añadir más casos si necesitas importar luego
        return "redirect:/calendar";
    } catch (Exception e) {
        System.out.println("❌ Error en el callback de Google Calendar: " + e.getMessage());
        return "redirect:/calendar?errorCallback";
    }
}


@GetMapping("/google/auth")
public void redirectToGoogleAuth(HttpServletResponse response) throws IOException, GeneralSecurityException {
    // código que redirige a la página de Google para login
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

