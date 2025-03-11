package com.isst.ISST_Grupo25_Casas.controllers;

import com.isst.ISST_Grupo25_Casas.services.GoogleCalendarService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import com.google.api.services.calendar.model.Event;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    /**
     * Devuelve la página del calendario.
     */
    @GetMapping
    public String calendarPage() {
        return "calendar";
    }

    /**
     * Redirige al usuario a la autenticación de Google Calendar.
     */
    @GetMapping("/auth")
    public void googleAuth(HttpServletResponse response) throws IOException {
        String authUrl = "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=https://www.googleapis.com/auth/calendar.events https://www.googleapis.com/auth/calendar.readonly";
        response.sendRedirect(authUrl);
    }

    /**
     * Callback de Google OAuth: Intercambia el código por un token de acceso.
     */
    @GetMapping("/callback")
    public String oauthCallback(@RequestParam("code") String code, HttpSession session) {
        try {
            String token = GoogleCalendarService.exchangeCodeForToken(code, clientId, clientSecret, redirectUri);
            session.setAttribute("googleAccessToken", token);
            return "redirect:/calendar";
        } catch (IOException e) {
            e.printStackTrace();
            return "error"; // Página de error si falla la autenticación
        }
    }

    /**
     * Obtiene los eventos del usuario desde Google Calendar.
     */
    @GetMapping("/events")
    @ResponseBody
    public ResponseEntity<List<Event>> getEvents(HttpSession session) {
        try {
            String token = (String) session.getAttribute("googleAccessToken");
            if (token == null) {
                return ResponseEntity.status(401).build(); // No autorizado
            }
            List<Event> events = GoogleCalendarService.fetchEvents();
            return ResponseEntity.ok(events);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build(); // Error interno del servidor
        }
    }

    /**
     * Crea un evento en Google Calendar.
     */
    @PostMapping("/events")
    @ResponseBody
    public ResponseEntity<String> createEvent(@RequestBody Event event, HttpSession session) {
        try {
            String token = (String) session.getAttribute("googleAccessToken");
            if (token == null) {
                return ResponseEntity.status(401).body("No autorizado. Inicia sesión con Google.");
            }

            GoogleCalendarService.createEvent(
                event.getSummary(),
                event.getDescription(),
                event.getStart().getDateTime().toString(),
                event.getEnd().getDateTime().toString()
            );

            return ResponseEntity.ok("Evento creado correctamente.");
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al crear el evento.");
        }
    }
}
