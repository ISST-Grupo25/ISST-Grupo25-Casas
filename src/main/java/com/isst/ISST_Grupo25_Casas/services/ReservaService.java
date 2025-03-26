package com.isst.ISST_Grupo25_Casas.services;

import com.isst.ISST_Grupo25_Casas.models.Huesped;
import com.isst.ISST_Grupo25_Casas.models.Reserva;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.isst.ISST_Grupo25_Casas.models.Cerradura;
import com.isst.ISST_Grupo25_Casas.models.Gestor;
import com.isst.ISST_Grupo25_Casas.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
//import java.util.Date;
import java.sql.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    public List<Reserva> obtenerTodasLasReservas() {
        return reservaRepository.findAll();
    }

    @Transactional
    public Reserva guardarReserva(Date fechaInicio, Date fechaFin, Cerradura cerradura, List<Huesped> huespedes, Gestor gestor) {



        Reserva reserva = new Reserva();
            reserva.setFechainicio(fechaInicio);
            reserva.setFechafin(fechaFin);
            reserva.setCerradura(cerradura);
            reserva.setHuespedes(huespedes);
            reserva.setPin(String.valueOf((int) (Math.random() * 9000) + 1000));
            reserva.setGestor(gestor);

        return reservaRepository.save(reserva);
    }

    public List<Reserva> obtenerReservasPorHuesped(Long huespedId) {
        return reservaRepository.findByHuespedesId(huespedId);
    }

    public List<Reserva> obtenerReservasPorCerradura(Long cerraduraId) {
        return reservaRepository.findByCerraduraId(cerraduraId);
    }


    public int importarDesdeGoogle(Cerradura cerradura, Huesped huesped, Gestor gestor) {
        int importadas = 0;
        try {
            Calendar calendar = GoogleCalendarService.getCalendarService();
    
            List<Event> eventos = calendar.events().list("primary")
                    .setTimeMin(new DateTime(System.currentTimeMillis()))
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute()
                    .getItems();
    
            for (Event event : eventos) {
                if (event.getStart() != null && event.getEnd() != null) {
                    Date fechaInicio = new Date(event.getStart().getDateTime().getValue());
                    Date fechaFin = new Date(event.getEnd().getDateTime().getValue());
    
                    boolean existe = existeReservaEnEseRangoYCasa(fechaInicio, fechaFin, cerradura);
    
                    if (!existe) {
                        List<Huesped> huespedes = new ArrayList<>();
                        huespedes.add(huesped);
    
                        guardarReserva(fechaInicio, fechaFin, cerradura, huespedes, gestor);
                        importadas++;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error al importar eventos: " + e.getMessage());
        }
        return importadas;
    }

    public boolean existeReservaEnEseRangoYCasa(Date inicio, Date fin, Cerradura cerradura) {
        List<Reserva> reservas = obtenerReservasPorCerradura(cerradura.getId());
    
        for (Reserva r : reservas) {
            boolean solapan = !(fin.before(r.getFechainicio()) || inicio.after(r.getFechafin()));
            if (solapan) {
                return true;
            }
        }
        return false;
    }

}

