// ReservaController.java -----------------------------------------------------------
package com.isst.ISST_Grupo25_Casas.controllers;
import com.isst.ISST_Grupo25_Casas.models.Reserva;
import com.google.api.client.util.DateTime;
import com.isst.ISST_Grupo25_Casas.models.Cerradura;
import com.isst.ISST_Grupo25_Casas.models.Gestor;
import com.isst.ISST_Grupo25_Casas.models.Huesped;
import com.isst.ISST_Grupo25_Casas.services.CerraduraService;
import com.isst.ISST_Grupo25_Casas.services.HuespedService;
import com.isst.ISST_Grupo25_Casas.services.ReservaService;
//import com.isst.ISST_Grupo25_Casas.utils.IcsParserUtil;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
    
        if (obj instanceof Huesped huesped) {
            List<Reserva> activas = reservaService.obtenerReservasActivasOFuturasPorHuesped(huesped);
            List<Reserva> antiguas = reservaService.obtenerReservasAntiguasPorHuesped(huesped);
    
            Map<Long, Boolean> estadoReservas = new HashMap<>();
            LocalDate hoy = LocalDate.now();
    
            for (Reserva r : activas) {
                boolean activa = !r.getFechainicio().after(Date.valueOf(hoy)) && !r.getFechafin().before(Date.valueOf(hoy));
                estadoReservas.put(r.getId(), activa);
            }
    
            model.addAttribute("reservas", activas);
            model.addAttribute("reservasAntiguas", antiguas);
            System.out.println("Antiguas: " + antiguas.size());
            model.addAttribute("estadoReservas", estadoReservas);
            return "home-access";
        }
    
        return "redirect:/login";
    }


    @GetMapping("/calendar")
    public String mostrarFormularioReserva(Model model, HttpSession session) {

            Object obj = session.getAttribute("usuario");

            if (obj instanceof Gestor gestor) { // Validar y castear correctamente
                List<Reserva> reservas = reservaService.obtenerReservasPorGestor(gestor.getId());
                List<Cerradura> cerraduras = cerraduraService.obtenerCerradurasPorGestor(gestor.getId());
                List<Huesped> huespedes = huespedService.obtenerTodosLosHuespedes();
        
                if (cerraduras.isEmpty()) {
                    System.out.println("⚠️ No hay cerraduras registradas para este gestor");
                }
                if (huespedes.isEmpty()) {
                    System.out.println("⚠️ No hay huéspedes registrados en la BD");
                }
        
                model.addAttribute("reservas", reservas);
                model.addAttribute("cerraduras", cerraduras);
                model.addAttribute("huespedes", huespedes);
            } else {
                System.out.println("❌ Error: No hay un gestor en sesión");
                return "redirect:/login";
            }
            
            return "calendar"; // Carga la vista "calendar.html"
    }

    @PostMapping("/calendar/guardar")
    public String guardarReserva(@RequestParam("casa") Long cerraduraId,
                                 @RequestParam("fechaInicio") String fechaInicioStr,
                                 @RequestParam("fechaFin") String fechaFinStr,
                                 @RequestParam(value = "huespedes", required = false) List<Long> huespedIds,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        System.out.println("📥 Huespedes recibidos: " + huespedIds);
        try {
            Object obj = session.getAttribute("usuario");
            if (!(obj instanceof Gestor gestor)) {
                System.out.println("⚠️ No hay un usuario gestor en sesión ");
                return "redirect:/calendar?error";
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


        return "redirect:/calendar";

    } catch (Exception e) {
        System.out.println("❌ Error al guardar reserva: " + e.getMessage());
        return "redirect:/calendar?error";
    }
}




@GetMapping("/google-calendar/authorize")
public String googleCalendarAuthorize() {
    try {
        String url = GoogleCalendarService.getAuthorizationUrl(); // El ID de usuario puede ser "isst"
        return "redirect:" + url;
    } catch (Exception e) {
        System.out.println("❌ Error al generar URL de autorización: " + e.getMessage());
        return "redirect:/calendar?errorGoogleAuth";
    }
}

@GetMapping("/google-calendar/callback")
public String googleCalendarCallback(@RequestParam("code") String code, HttpSession session) {
    try {
        GoogleCalendarService.autorizarConCodigo(code);

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
        }else if ("sincronizar".equals(pending)) {
            Gestor gestor = (Gestor) session.getAttribute("usuario");
            session.removeAttribute("pendingAction");

            List<Reserva> reservas = reservaService.obtenerReservasPorGestor(gestor.getId());
            GoogleCalendarService.sincronizarConGoogle(reservas);

            return "redirect:/calendar?syncSuccess";
        }

        // Puedes añadir más casos si necesitas importar luego
        return "redirect:/calendar";
    } catch (Exception e) {
        System.out.println("❌ Error en el callback de Google Calendar: " + e.getMessage());
        return "redirect:/calendar?errorCallback";
    }
}



    //la prioridad en la sincronización es la base de datos, por lo que si hay un evento en Google Calendar y no en la base de datos, se eliminará el evento de Google Calendar
    @PostMapping("/calendar/sincronizar")
public String sincronizarConGoogle(HttpSession session, RedirectAttributes redirectAttributes) {
    try {
        Object obj = session.getAttribute("usuario");
        if (obj instanceof Gestor gestor) {
            List<Reserva> reservas = reservaService.obtenerReservasPorGestor(gestor.getId());
            GoogleCalendarService.sincronizarConGoogle(reservas);
            redirectAttributes.addFlashAttribute("sincronizado", true);
            return "redirect:/calendar";
        } else {
            return "redirect:/calendar?error=NoAutenticado";
        }
    } catch (RuntimeException e) {
        if (e.getMessage().contains("reautenticación")) {
            // 👉 Guardamos acción pendiente y redirigimos a autorización
            session.setAttribute("pendingAction", "sincronizar");
            return "redirect:/google-calendar/authorize";
        } else {
            redirectAttributes.addFlashAttribute("errorSincronizar", e.getMessage());
            return "redirect:/calendar";
        }
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorSincronizar", "Error al sincronizar: " + e.getMessage());
        return "redirect:/calendar";
    }
}




    // Método para generar un color único basado en el ID de la cerradura y mostrarlo luego en el calendario
    private String generarColorDesdeId(Long id) {
        if (id == null) return "#999999";
    
        // Generar un color HSL basado en el ID
        float hue = (id * 137) % 360; // 137 es un número primo para mejor dispersión
        float saturation = 0.7f;      // Saturación 70%
        float lightness = 0.6f;       // Luminosidad 60%
    
        return hslToHex(hue, saturation, lightness);
    }
    
    // Conversión de HSL a HEX
    private String hslToHex(float h, float s, float l) {
        float c = (1 - Math.abs(2 * l - 1)) * s;
        float x = c * (1 - Math.abs((h / 60) % 2 - 1));
        float m = l - c / 2;
    
        float r = 0, g = 0, b = 0;
        if (h < 60) {
            r = c; g = x; b = 0;
        } else if (h < 120) {
            r = x; g = c; b = 0;
        } else if (h < 180) {
            r = 0; g = c; b = x;
        } else if (h < 240) {
            r = 0; g = x; b = c;
        } else if (h < 300) {
            r = x; g = 0; b = c;
        } else {
            r = c; g = 0; b = x;
        }
    
        int rInt = Math.round((r + m) * 255);
        int gInt = Math.round((g + m) * 255);
        int bInt = Math.round((b + m) * 255);
    
        return String.format("#%02X%02X%02X", rInt, gInt, bInt);
    }

    @GetMapping("/reservas")
        @ResponseBody
        public List<Map<String, Object>> obtenerReservasParaCalendario(HttpSession session) {
            Object obj = session.getAttribute("usuario");

            if (obj instanceof Gestor gestor) {
                List<Reserva> reservas = reservaService.obtenerReservasPorGestor(gestor.getId());
                List<Map<String, Object>> eventos = new ArrayList<>();

                for (Reserva r : reservas) {
                    Map<String, Object> evento = new HashMap<>();
                    evento.put("id", r.getId()); // ✅ Añadir esto
                    evento.put("title", "Reserva casa " + r.getCerradura().getUbicacion());
                    evento.put("start", r.getFechainicio().toString());
                    
                    LocalDate fechaFin = r.getFechafin().toLocalDate(); // 🔥 Convertimos java.sql.Date a LocalDate
                    fechaFin = fechaFin.plusDays(1); // 🔥 Sumamos 1 día directamente
                    evento.put("end", fechaFin.toString()); //
                    evento.put("allDay", true);

                    evento.put("extendedProps", Map.of(
                        "cerraduraId", r.getCerradura().getId(),
                        "fechaFinReal", r.getFechafin().toString(),
                        "huespedes", r.getHuespedes().stream().map(h -> Map.of("id", h.getId(), "nombre", h.getNombre())).toList()
                    ));
                    evento.put("color", generarColorDesdeId(r.getCerradura().getId()));
                    
                    eventos.add(evento);
                }

                return eventos;

            } else {
                return new ArrayList<>(); // Retorna una lista vacía si no hay un gestor en sesión
            }
        }


    @PostMapping("/calendar/editar-modal")
    public String actualizarReservaDesdeModal(@RequestParam Long id,
                                              @RequestParam("casa") Long cerraduraId,
                                              @RequestParam("fechaInicio") String fechaInicioStr,
                                              @RequestParam("fechaFin") String fechaFinStr,
                                              @RequestParam("huespedes") List<Long> huespedIds,
                                              RedirectAttributes redirectAttributes) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaInicio = new Date(sdf.parse(fechaInicioStr).getTime());
            Date fechaFin = new Date(sdf.parse(fechaFinStr).getTime());
    
            Cerradura cerradura = cerraduraService.obtenerCerraduraPorId(cerraduraId);
            List<Huesped> huespedes = huespedService.obtenerHuespedesPorIds(huespedIds);
    
            reservaService.actualizarReserva(id, fechaInicio, fechaFin, cerradura, huespedes);
            redirectAttributes.addFlashAttribute("editado", true);
            return "redirect:/calendar";
        } catch (Exception e) {
            return "redirect:/calendar?errorEdicion";
        }
    }

    @PostMapping("/calendar/eliminar")
    public String eliminarReserva(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            reservaService.eliminarReserva(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "✅ Reserva eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorEliminar", "No se pudo eliminar la reserva");
        }
        return "redirect:/calendar";
    }
        



}

