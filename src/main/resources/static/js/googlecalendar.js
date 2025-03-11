const { google } = require("googleapis");
const fs = require("fs");

async function authorize() {
  const auth = new google.auth.GoogleAuth({
    keyFile: "credentials.json",
    scopes: ["https://www.googleapis.com/auth/calendar"],
  });
  return auth;
}

async function createEvent() {
  const auth = await authorize();
  const calendar = google.calendar({ version: "v3", auth });

  const event = {
    summary: "Reunión de prueba",
    start: {
      dateTime: "2025-03-12T10:00:00-05:00",
      timeZone: "America/Mexico_City",
    },
    end: {
      dateTime: "2025-03-12T11:00:00-05:00",
      timeZone: "America/Mexico_City",
    },
  };

  calendar.events.insert(
    { calendarId: "primary", resource: event },
    (err, res) => {
      if (err) return console.error("Error creando evento:", err);
      console.log("Evento creado:", res.data);
    }
  );
}

createEvent();
