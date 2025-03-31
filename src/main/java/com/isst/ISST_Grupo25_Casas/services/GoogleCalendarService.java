package com.isst.ISST_Grupo25_Casas.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.isst.ISST_Grupo25_Casas.models.Reserva;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Map; // Ensure Map is imported
import java.util.TimeZone;

public class GoogleCalendarService {
    private static final String APPLICATION_NAME = "IoH";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    public static GoogleAuthorizationCodeFlow getFlow() throws IOException, GeneralSecurityException {
        InputStream in = GoogleCalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("No se encontró el archivo credentials.json");
        }
    
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
    
        return new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                clientSecrets,
                SCOPES
        ).setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
         .setAccessType("offline")
         .build();
    }

    /**
     * Obtiene una instancia del servicio de Google Calendar.
     */
    public static Calendar getCalendarService() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GoogleAuthorizationCodeFlow flow = getFlow();
        Credential credential = flow.loadCredential("isst");
    
        // Diagnóstico completo del token
        if (credential == null) {
            System.out.println("🟥 No se encontró ninguna credencial guardada para 'isst'.");
            throw new RuntimeException("No autorizado con Google Calendar, se necesita reautenticación.");
        }
    
        System.out.println("📄 Token encontrado:");
        System.out.println("🔑 Access Token: " + credential.getAccessToken());
        System.out.println("🔄 Refresh Token: " + credential.getRefreshToken());
    
        Long expiresInSeconds = credential.getExpiresInSeconds();
        if (expiresInSeconds != null) {
            System.out.println("⏳ El token expira en " + expiresInSeconds + " segundos.");
            if (expiresInSeconds <= 60) {
                System.out.println("⚠️ El token está caducado o a punto de caducar. Eliminando...");
                boolean deleted = flow.getCredentialDataStore().delete("isst") != null;
                System.out.println("🗑 ¿Token eliminado?: " + deleted);
                throw new RuntimeException("No autorizado con Google Calendar, se necesita reautenticación.");
            } else {
                System.out.println("✅ El token está activo.");
            }
        } else {
            System.out.println("❌ No se pudo obtener la expiración del token. Eliminando por seguridad...");
            boolean deleted = flow.getCredentialDataStore().delete("isst") != null;
            System.out.println("🗑 ¿Token eliminado?: " + deleted);
            throw new RuntimeException("No autorizado con Google Calendar, se necesita reautenticación.");
        }
    
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("IoH")
                .build();
    }
    

    /**
     * Intercambia un código de autorización por un token de acceso.
     */
    public static String exchangeCodeForToken(String code, String clientId, String clientSecret, String redirectUri) throws IOException {
        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(),
                JSON_FACTORY,
                clientId,
                clientSecret,
                Collections.singletonList(CalendarScopes.CALENDAR))
                .build()
                .newTokenRequest(code)
                .setRedirectUri(redirectUri)
                .execute();

        return tokenResponse.getAccessToken();
    }

    /**
     * Obtiene los eventos del calendario del usuario.
     */
    public static List<Event> fetchEvents() throws IOException, GeneralSecurityException {
        Calendar service = getCalendarService();
        return service.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(new com.google.api.client.util.DateTime(System.currentTimeMillis()))
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute()
                .getItems();
    }

    /**
     * Crea un evento en Google Calendar.
     */
    public static Event createEvent(String summary, String description, String startDateTime, String endDateTime) throws IOException, GeneralSecurityException {
        Calendar service = getCalendarService();

        Event event = new Event()
                .setSummary(summary)
                .setDescription(description);

        EventDateTime start = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(startDateTime))
                .setTimeZone(TimeZone.getDefault().getID());
        event.setStart(start);

        EventDateTime end = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(endDateTime))
                .setTimeZone(TimeZone.getDefault().getID());
        event.setEnd(end);

        return service.events().insert("primary", event).execute();
    }

    /**
 * Actualiza un evento existente en Google Calendar.
 */
// public static Event updateEvent(String eventId, String summary, String description, String startDateTime, String endDateTime) throws IOException, GeneralSecurityException {
//     Calendar service = getCalendarService();

//     // Obtener el evento existente
//     Event event = service.events().get("primary", eventId).execute();

//     // Actualizar campos
//     event.setSummary(summary);
//     event.setDescription(description);

//     EventDateTime start = new EventDateTime()
//             .setDateTime(new com.google.api.client.util.DateTime(startDateTime))
//             .setTimeZone(TimeZone.getDefault().getID());
//     event.setStart(start);

//     EventDateTime end = new EventDateTime()
//             .setDateTime(new com.google.api.client.util.DateTime(endDateTime))
//             .setTimeZone(TimeZone.getDefault().getID());
//     event.setEnd(end);

//     // Ejecutar actualización
//     return service.events().update("primary", event.getId(), event).execute();
// }

public static void sincronizarConGoogle(List<Reserva> reservas) throws IOException, GeneralSecurityException {
    Calendar service = getCalendarService();

    // 1. Obtener eventos existentes que sean de la app (marcados con "source=IoH")
    List<Event> eventosExistentes = service.events().list("primary")
            .setMaxResults(2500)
            .setTimeMin(new DateTime(System.currentTimeMillis()))
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute()
            .getItems();

    // 2. Filtrar y eliminar solo los que fueron creados por la app
    for (Event e : eventosExistentes) {
        Map<String, String> props = e.getExtendedProperties() != null ? e.getExtendedProperties().getPrivate() : null;

        if (props != null && "IoH".equals(props.get("source"))) {
            try {
                service.events().delete("primary", e.getId()).execute();
            } catch (IOException ex) {
                System.out.println("⚠️ No se pudo eliminar el evento " + e.getId() + ": " + ex.getMessage());
            }
        }
    }

    // 3. Crear eventos desde reservas con la marca "source=IoH"
    for (Reserva reserva : reservas) {
        Event event = new Event()
                .setSummary("Reserva casa " + reserva.getCerradura().getUbicacion())
                .setDescription("Reserva desde la app IoH. PIN: " + reserva.getPin());

        event.setExtendedProperties(new Event.ExtendedProperties()
                .setPrivate(Collections.singletonMap("source", "IoH")));

        EventDateTime start = new EventDateTime()
                .setDateTime(new DateTime(reserva.getFechainicio()))
                .setTimeZone(TimeZone.getDefault().getID());
        event.setStart(start);

        EventDateTime end = new EventDateTime()
                .setDateTime(new DateTime(reserva.getFechafin()))
                .setTimeZone(TimeZone.getDefault().getID());
        event.setEnd(end);

        service.events().insert("primary", event).execute();
    }

    System.out.println("✅ Sincronización completa con Google Calendar.");
}



public static String getAuthorizationUrl() throws IOException, GeneralSecurityException {
    GoogleAuthorizationCodeFlow flow = getFlow();
    return flow.newAuthorizationUrl().setRedirectUri(getRedirectUri()).set("prompt", "consent").build();
}
    
public static void autorizarConCodigo(String code) throws Exception {
    GoogleAuthorizationCodeFlow flow = getFlow();
    TokenResponse response = flow.newTokenRequest(code)
            .setRedirectUri(getRedirectUri())
            .execute();

    flow.createAndStoreCredential(response, "isst"); // Usamos una clave fija
}

    
    private static String getRedirectUri() {
        return "http://localhost:8080/google-calendar/callback"; // Asegúrate que este coincide con lo de tu consola de Google
    }
    

}