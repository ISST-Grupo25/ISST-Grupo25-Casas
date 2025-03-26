package com.isst.ISST_Grupo25_Casas.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

public class GoogleCalendarService {
    private static final String APPLICATION_NAME = "IoH";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Carga las credenciales para acceder a Google Calendar API.
     */
    private static Credential getCredentials(final HttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = GoogleCalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("El archivo 'credentials.json' no se encontró en " + CREDENTIALS_FILE_PATH);
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        return flow.loadCredential("user");
    }

    /**
     * Obtiene una instancia del servicio de Google Calendar.
     */
    public static Calendar getCalendarService() throws IOException, GeneralSecurityException {
        HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = getCredentials(HTTP_TRANSPORT);
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
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
public static Event updateEvent(String eventId, String summary, String description, String startDateTime, String endDateTime) throws IOException, GeneralSecurityException {
    Calendar service = getCalendarService();

    // Obtener el evento existente
    Event event = service.events().get("primary", eventId).execute();

    // Actualizar campos
    event.setSummary(summary);
    event.setDescription(description);

    EventDateTime start = new EventDateTime()
            .setDateTime(new com.google.api.client.util.DateTime(startDateTime))
            .setTimeZone(TimeZone.getDefault().getID());
    event.setStart(start);

    EventDateTime end = new EventDateTime()
            .setDateTime(new com.google.api.client.util.DateTime(endDateTime))
            .setTimeZone(TimeZone.getDefault().getID());
    event.setEnd(end);

    // Ejecutar actualización
    return service.events().update("primary", event.getId(), event).execute();
}


}
