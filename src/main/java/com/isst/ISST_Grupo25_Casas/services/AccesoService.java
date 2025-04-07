package com.isst.ISST_Grupo25_Casas.services;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isst.ISST_Grupo25_Casas.models.Acceso;
import com.isst.ISST_Grupo25_Casas.models.Cerradura;
import com.isst.ISST_Grupo25_Casas.models.Acceso;
import com.isst.ISST_Grupo25_Casas.models.Gestor;
import com.isst.ISST_Grupo25_Casas.models.Huesped;
import com.isst.ISST_Grupo25_Casas.models.Reserva;
import com.isst.ISST_Grupo25_Casas.repository.AccesoRepository;

@Service
public class AccesoService {

    @Autowired
    private AccesoRepository accesoRepository;

    public List<Acceso> obtenerTodasLasAccesos() {
        return accesoRepository.findAll();
    }

    // 🔵 Buscar accesos de un huesped
    public List<Acceso> obtenerAccesosPorHuesped(Huesped huesped) {
        return accesoRepository.findByHuesped(huesped);
    }

    // 🔵 Buscar accesos de una reserva
    public List<Acceso> obtenerAccesosPorReserva(Reserva reserva) {
        return accesoRepository.findByReserva(reserva);
    }

    public Acceso guardarAcceso(Date horario, Boolean resultado, Huesped huesped, Reserva reserva) {
        Acceso acceso = new Acceso();
        acceso.setHorario(horario);
        acceso.setResultado(resultado);

        acceso.setHuesped(huesped);
        acceso.setReserva(reserva);
    
        return accesoRepository.save(acceso);
    }
}
