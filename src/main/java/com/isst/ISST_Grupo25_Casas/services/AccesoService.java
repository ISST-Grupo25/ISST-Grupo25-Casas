package com.isst.ISST_Grupo25_Casas.services;


import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Schema.Printer;
import org.springframework.stereotype.Service;

import com.isst.ISST_Grupo25_Casas.models.Acceso;
import com.isst.ISST_Grupo25_Casas.models.Cerradura;
import com.isst.ISST_Grupo25_Casas.models.Acceso;
import com.isst.ISST_Grupo25_Casas.models.Gestor;
import com.isst.ISST_Grupo25_Casas.models.Huesped;
import com.isst.ISST_Grupo25_Casas.models.Reserva;
import com.isst.ISST_Grupo25_Casas.repository.AccesoRepository;

import java.util.Comparator;

@Service
public class AccesoService {

    @Autowired
    private AccesoRepository accesoRepository;

    public List<Acceso> obtenerTodasLasAccesos() {
        return accesoRepository.findAll();
    }

    public long contarNoLeidosPorGestor(Long gestorId) {
        return accesoRepository.countByGestorIdAndLeidoFalse(gestorId);
    }

    // 🔵 Buscar accesos de un huesped
    public List<Acceso> obtenerAccesosPorHuesped(Huesped huesped) {
        return accesoRepository.findByHuesped(huesped);
    }

    public Acceso guardarAcceso(Date horario, Boolean resultado, Huesped huesped, Reserva reserva) {
        Acceso acceso = new Acceso();
        acceso.setHorario(horario);
        acceso.setResultado(resultado);

        acceso.setHuesped(huesped);
        acceso.setReserva(reserva);
    
        return accesoRepository.save(acceso);
    }

    public List<Acceso> obtenerAccesosPorReserva(Long reservaId) {
        List<Acceso> accesos = accesoRepository.findAllByReservaId(reservaId);
        System.out.println("ID de reserva: " + reservaId + accesos);
        return accesoRepository.findAllByReservaId(reservaId);
    }

    public List<Acceso> obtenerAccesosPorReservas(List<Reserva> reservas) {
        // Extraer los IDs de las reservas
        List<Long> reservaIds = reservas.stream()
                .map(Reserva::getId)
                .toList();
    
        // Consultar los accesos directamente desde el repositorio
        return accesoRepository.findByReservaIdIn(reservaIds);
    }

    /** Devuelve solo los NO-LEÍDOS, ordenados */
    public List<Acceso> obtenerAccesosNoLeidos(List<Reserva> reservas) {
        List<Long> ids = reservas.stream().map(Reserva::getId).toList();
        return accesoRepository.findAllByReservaIdInAndLeidoFalse(ids)
                               .stream()
                               .sorted(Comparator.comparing(Acceso::getHorario).reversed())
                               .toList();
    }
    /** Marca un acceso como leído */
    public void marcarLeido(Long accesoId) {
        accesoRepository.findById(accesoId).ifPresent(a -> {
            a.setLeido(true);
            accesoRepository.save(a);
        });
    }
    /** Marca todos los de la lista como leídos */
    public void marcarTodosLeidos(List<Acceso> accesos) {
        accesos.forEach(a -> a.setLeido(true));
        accesoRepository.saveAll(accesos);
    }
}