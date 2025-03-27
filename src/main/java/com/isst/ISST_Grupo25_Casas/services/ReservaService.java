package com.isst.ISST_Grupo25_Casas.services;

import com.isst.ISST_Grupo25_Casas.models.Reserva;
import com.isst.ISST_Grupo25_Casas.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    public List<Reserva> obtenerReservasPorHuesped(Long huespedId) {
        return reservaRepository.findByHuespedesId(huespedId);
    }
}
