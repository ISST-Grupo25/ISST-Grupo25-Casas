package com.isst.ISST_Grupo25_Casas.utils;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.DtEnd;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class IcsParserUtil {

    public static List<Timestamp[]> parseFechas(InputStream icsInputStream) {
        List<Timestamp[]> fechas = new ArrayList<>();

        try {
            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendar = builder.build(icsInputStream);

            for (Object obj : calendar.getComponents(Component.VEVENT)) {
                VEvent event = (VEvent) obj;
                DtStart dtStart = event.getStartDate();
                DtEnd dtEnd = event.getEndDate();

                if (dtStart != null && dtEnd != null) {
                    Timestamp inicio = new Timestamp(dtStart.getDate().getTime());
                    Timestamp fin = new Timestamp(dtEnd.getDate().getTime());
                    fechas.add(new Timestamp[]{inicio, fin});
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
								System.out.println("Fechas extraídas del archivo ICS: " + fechas.size());
        return fechas;
    }
}


