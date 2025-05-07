// ReservaService.java -----------------------------------------------------------
package com.isst.ISST_Grupo25_Casas.services;

import com.isst.ISST_Grupo25_Casas.models.Huesped;
import com.isst.ISST_Grupo25_Casas.models.Reserva;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.isst.ISST_Grupo25_Casas.models.Cerradura;
import com.isst.ISST_Grupo25_Casas.models.Gestor;
import com.isst.ISST_Grupo25_Casas.repository.ReservaRepository;
import com.isst.ISST_Grupo25_Casas.repository.HuespedRepository;
//import com.isst.ISST_Grupo25_Casas.utils.IcsParserUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;
//import java.util.Date;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservaService {

    @Autowired
    private HuespedRepository huespedRepository;

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

         // Actualizamos el lado de Huesped para mantener la relación bidireccional y evitar problemas de sincronización en la bbdd
        for (Huesped h : huespedes) {
            h.getReservas().add(reserva);
        }

        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva actualizarReserva(Long id, Date fechaInicio, Date fechaFin, Cerradura cerradura, List<Huesped> nuevosHuespedes) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada con ID: " + id));

        // Limpiar huéspedes anteriores para actualizar la relación bidireccional
        for (Huesped h : reserva.getHuespedes()) {
            h.getReservas().remove(reserva);
        }

        reserva.setFechainicio(fechaInicio);
        reserva.setFechafin(fechaFin);
        reserva.setCerradura(cerradura);
        reserva.setHuespedes(nuevosHuespedes);

        for (Huesped h : nuevosHuespedes) {
            h.getReservas().add(reserva);
        }

        return reservaRepository.save(reserva);
    }

    @Transactional
    public void eliminarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada con ID: " + id));

        // Eliminar la relación bidireccional con los huéspedes
        for (Huesped h : reserva.getHuespedes()) {
            h.getReservas().remove(reserva);
        }

        reservaRepository.delete(reserva);
    }



    public List<Reserva> obtenerReservasPorHuesped(Long huespedId) {
        return reservaRepository.findByHuespedesId(huespedId);
    }

    public List<Reserva> obtenerReservasPorCerradura(Long cerraduraId) {
        return reservaRepository.findByCerraduraId(cerraduraId);
    }

    public List<Reserva> obtenerReservasPorGestor(Long gestorId) {
        return reservaRepository.findByGestorId(gestorId);
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


    public List<Reserva> obtenerProximasReservasPorCerradura(Long cerraduraId) {
        List<Reserva> reservas = reservaRepository.findByCerraduraId(cerraduraId);
        List<Reserva> proximasReservas = new ArrayList<>();

        for (Reserva reserva : reservas) {
            if (reserva.getFechafin().after(new Date(System.currentTimeMillis()))) {
                proximasReservas.add(reserva);
            }
        }

        return proximasReservas;
    }

    @Transactional
    public void eliminarReservaYHuespedes(Long reservaId) {
        reservaRepository.eliminarHuespedesDeReserva(reservaId); // 🔥 Primero borrar los enlaces
        reservaRepository.deleteById(reservaId); // 🔥 Después borrar la reserva
    }

    public void eliminarReservasPorGestor(Long gestorId) {
        List<Reserva> reservas = reservaRepository.findByGestorId(gestorId);
        for (Reserva reserva : reservas) {
            eliminarReservaYHuespedes(reserva.getId());
        }
    }

    public List<Reserva> obtenerReservasActivasOFuturasPorHuesped(Huesped huesped) {
        return reservaRepository.findReservasActivasOFuturasPorHuesped(huesped);
    }

    public List<Reserva> obtenerReservasAntiguasPorHuesped(Huesped huesped) {
        LocalDate hoy = LocalDate.now();
        return reservaRepository.findByHuespedesContainingAndFechafinBefore(huesped, Date.valueOf(hoy));
    }

    @Transactional
    public void asociarHuesped(Long reservaId, Long huespedId) {
        Reserva reserva = reservaRepository.findById(reservaId)
            .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        Huesped huesped = huespedRepository.findById(huespedId)
            .orElseThrow(() -> new IllegalArgumentException("Huésped no encontrado"));

        // Evita lazy loading: no uses .contains
        reserva.getHuespedes().add(huesped);

        reservaRepository.save(reserva);
    }



}

