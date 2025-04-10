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

   @Autowired
   private ReservaService reservaService; // Add ReservaService dependency

   public CerraduraController(CerraduraService cerraduraService) {
       this.cerraduraService = cerraduraService;
       this.reservaRepository = reservaRepository;
   }

   @PostMapping("/cerradura/guardar")
   @ResponseBody //  esto es CLAVE
   public String guardarCerradura(@RequestParam("ubicacion") String ubicacion,
                                  @RequestParam("token") String token,
                                  HttpSession session) {
       try {
           Object obj = session.getAttribute("usuario");
           if (obj instanceof Gestor gestor) {
               // Guardar y recuperar la cerradura guardada
               Cerradura nuevaCerradura = cerraduraService.guardarCerradura(ubicacion, token, gestor.getId());
               
               //  Devolver directamente el ID como texto
               return String.valueOf(nuevaCerradura.getId());
           } else {
               System.out.println("❌ Error: No hay un gestor en sesión");
               return "-1"; // devolvemos error de forma sencilla
           }
       } catch (Exception e) {
           System.out.println("❌ Error al guardar cerradura: " + e.getMessage());
           return "-1"; // devolvemos error también aquí
       }
   }

   @PostMapping("/cerradura/abrir")
   public String abrirCerradura(@RequestParam("pin") String pin,
                                @RequestParam("reservaId") Long reservaId,
                                @RequestParam("cerraduraId") Long cerraduraId,
                                Model model,
                                RedirectAttributes redirectAttributes) {
       try {
           // Obtener la cerradura por su ID
           Cerradura cerradura = cerraduraService.obtenerCerraduraPorId(cerraduraId);
           // Verificar el PIN
           if (esPinValido(pin, reservaId)) {
               // Generar un token
               String token = cerraduraService.obtenerTokenPorCerradura(cerradura);
               // Crear un objeto para enviar el token en formato JSON
               Map<String, String> requestBody = new HashMap<>();
               requestBody.put("token", token);
               // Enviar el token a Backend 2 para abrir la cerradura
               String backend2Url = "http://localhost:3555/abrirCerradura"; // URL de Backend 2
               ResponseEntity<String> response = restTemplate.exchange(
                   backend2Url,
                   HttpMethod.POST,
                   new HttpEntity<>(requestBody), // Enviar el token en el cuerpo de la solicitud
                   String.class
               );
                               // Parse the JSON response
               ObjectMapper mapper = new ObjectMapper();
               JsonNode responseJson = mapper.readTree(response.getBody());
               String estado = responseJson.get("estado").asText();
               if (response.getStatusCode().is2xxSuccessful() && "abierta".equals(estado)) {
                   model.addAttribute("message", "Puerta Abierta");
                   redirectAttributes.addFlashAttribute("success", true);
                   redirectAttributes.addFlashAttribute("message", "✅ Cerradura abierta correctamente");
                   System.out.println("✅ Cerradura abierta con éxito.");
                   return "redirect:/home-access?pinValido=true";
                   // Simulamos que la cerradura se cierra después de un tiempo (en este caso 10 segundos)
                   // Puedes enviar una solicitud para cerrarla si lo deseas
               } else {
                   model.addAttribute("message", "Error al abrir la puerta");
                   redirectAttributes.addFlashAttribute("errorMessage", "❌ Error al abrir cerradura: " + response.getBody());
                   System.out.println("❌ Error al abrir la cerradura:" + response.getBody());
               }
               return "redirect:/home-access"; // Redirigir al calendario
           } else {
               model.addAttribute("message", "PIN Inválido");
               return "redirect:/home-access"; // Mostrar error en la vista
           }
       } catch (Exception e) {
           System.out.println("❌ Error al abrir la cerradura: " + e.getMessage());
           return "redirect:/home-access"; // Mostrar error en la vista
       }
   }

   // Método para cerrar la cerradura desde Backend 1
   public void cerrarCerradura(String token) {
       String backend2Url = "http://localhost:3555/cerrarCerradura";
       Map<String, String> requestBody = new HashMap<>();
       requestBody.put("token", token);
       // Enviar el token a Backend 2 para cerrar la cerradura
       restTemplate.exchange(
           backend2Url,
           HttpMethod.POST,
           new HttpEntity<>(requestBody),
           String.class
       );
   }

   private boolean esPinValido(String pin, Long reservaId) {
       // Buscar la reserva por su ID
       Reserva reserva = reservaRepository.findById(reservaId).orElse(null);
       // Verificar si la reserva existe y si el PIN coincide
       if (reserva != null && reserva.getPin().equals(pin)) {
           System.out.println("✅ PIN válido para la reserva: " + reservaId);
           return true; // El PIN es válido para esta reserva
       }
       return false; // PIN inválido
   }
    
   
    @GetMapping("/homes")
    public String mostrarCerradurasConReservas(Model model, HttpSession session) {
        Object obj = session.getAttribute("usuario");
    
        if (obj instanceof Gestor gestor) {
            List<Cerradura> cerraduras = cerraduraService.obtenerCerradurasPorGestor(gestor.getId());
    
            // Para cada cerradura, obtenemos sus reservas
            Map<Long, List<Reserva>> reservasPorCerradura = new HashMap<>();
            for (Cerradura c : cerraduras) {
                List<Reserva> reservas = reservaService.obtenerProximasReservasPorCerradura(c.getId());
                if (reservas == null) { 
                    reservas = new ArrayList<>(); // 🚀 Si es null, ponemos una lista vacía
                }
                reservasPorCerradura.put(c.getId(), reservas);
            }
    
            model.addAttribute("cerraduras", cerraduras);
            model.addAttribute("reservasPorCerradura", reservasPorCerradura);
            return "homes"; // tu HTML homes.html
        }
    
        return "redirect:/login";
    }

    @GetMapping("reservasPorCerradura")
    public List<Reserva> obtenerReservasPorCerradura(@RequestParam("cerraduraId") Long cerraduraId) {
        // Obtener la cerradura por su ID
        Cerradura cerradura = cerraduraService.obtenerCerraduraPorId(cerraduraId);
        // Obtener las reservas asociadas a la cerradura
        List<Reserva> reservas = reservaRepository.findByCerraduraId(cerradura.getId());
        return reservas;
    }

    @PostMapping("/cerradura/eliminar")
    public String eliminarCerradura(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            cerraduraService.eliminarCerradura(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "✅ Casa eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "❌ Error al eliminar la casa.");
        }
        return "redirect:/homes"; // o donde tengas el listado de casas
    }


}