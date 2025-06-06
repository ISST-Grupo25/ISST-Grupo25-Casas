package com.isst.ISST_Grupo25_Casas.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.isst.ISST_Grupo25_Casas.models.Acceso;
import com.isst.ISST_Grupo25_Casas.models.Gestor;
import com.isst.ISST_Grupo25_Casas.models.Huesped;
import com.isst.ISST_Grupo25_Casas.models.Reserva;
import com.isst.ISST_Grupo25_Casas.models.Cerradura;
import com.isst.ISST_Grupo25_Casas.services.CerraduraService;
import com.isst.ISST_Grupo25_Casas.services.AccesoService;
import com.isst.ISST_Grupo25_Casas.services.ReservaService;
import java.util.Set;
import java.util.HashSet;

import java.util.Comparator;

import jakarta.servlet.http.HttpSession;




@Controller
public class NotificationsController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private AccesoService accesoService;

    @Autowired
    private CerraduraService cerraduraService;

    @GetMapping("/notifications")
    public String notifications(Model model, HttpSession session) {

        Object usuario = session.getAttribute("usuario");
        if (usuario == null) {
            System.out.println("❌ No hay un usuario en la sesión");
            return "redirect:/login"; // Redirigir a login si no está logueado
        }
        Object attr = session.getAttribute("descartados");
            Set<Long> descartados;

            if (attr instanceof Set) {
                descartados = (Set<Long>) attr;
            } else {
                descartados = new HashSet<>();
                session.setAttribute("descartados", descartados);
            }
        List<Reserva> reservas;
        if (usuario instanceof Gestor gestor) {
            // Obtener reservas asociadas al gestor
            reservas = reservaService.obtenerReservasPorGestor(gestor.getId());
            model.addAttribute("usuario", gestor);

            // 🟡 Buscar cerraduras con batería baja (<15%)
            List<Cerradura> cerraduras = cerraduraService.obtenerCerradurasPorGestor(gestor.getId());
            List<Cerradura> lowBatteryAlerts = cerraduras.stream()
                .filter(c -> c.getBateria() < 15)
                .toList();
            model.addAttribute("lowBatteryAlerts", lowBatteryAlerts);
            
        } else {
            System.out.println("❌ Usuario no autorizado");
            return "redirect:/login"; // Redirigir si el usuario no es válido
        }
          
                
        List<Acceso> accesos = accesoService.obtenerAccesosNoLeidos(reservas);
        model.addAttribute("accesos", accesos);
        System.out.println("🔵 Accesos: " + accesos);
        model.addAttribute("unreadCount", accesos.size());

        return "notifications";
    }

     @PostMapping("/notifications/dismiss/{id}")
  @ResponseBody
  public void dismissOne(@PathVariable Long id, HttpSession session) {
    @SuppressWarnings("unchecked")
    Set<Long> descartados = (Set<Long>) session.getAttribute("descartados");
    if (descartados == null) {
      descartados = new HashSet<>();
      session.setAttribute("descartados", descartados);
    }
    descartados.add(id);
    accesoService.marcarLeido(id);
  }

  // descartar todas las notificaciones de la vista
  @PostMapping("/notifications/dismissAll")
  @ResponseBody
  public void dismissAll(HttpSession session) {
    session.setAttribute("descartados", new HashSet<Long>());
    //  marcamos todos los no-leídos como leídos
        Gestor gestor = (Gestor) session.getAttribute("usuario");
        List<Reserva> reservas = reservaService.obtenerReservasPorGestor(gestor.getId());
        List<Acceso> accesos = accesoService.obtenerAccesosNoLeidos(reservas);
        accesoService.marcarTodosLeidos(accesos);
  }

}
