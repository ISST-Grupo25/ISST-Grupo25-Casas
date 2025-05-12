package com.isst.ISST_Grupo25_Casas.services;

import com.isst.ISST_Grupo25_Casas.models.Cerradura;
import com.isst.ISST_Grupo25_Casas.models.Gestor;
import com.isst.ISST_Grupo25_Casas.models.Reserva;
import com.isst.ISST_Grupo25_Casas.repository.CerraduraRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import com.isst.ISST_Grupo25_Casas.repository.ReservaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CerraduraService {
    private final CerraduraRepository cerraduraRepository;
    private final ReservaRepository reservaRepository;
    private final ReservaService reservaService;

    public CerraduraService(CerraduraRepository cerraduraRepository, ReservaRepository reservaRepository, ReservaService reservaService) {
        this.cerraduraRepository = cerraduraRepository;
        this.reservaRepository = reservaRepository;
        this.reservaService = reservaService;
    }
    

    public List<Cerradura> obtenerTodasLasCerraduras() {
        return cerraduraRepository.findAll();
    }

    public List<Cerradura> obtenerCerradurasPorGestor(Long gestorId) {
        return cerraduraRepository.findByGestorId(gestorId);
    }

    public Cerradura obtenerCerraduraPorId(Long id) {
        return cerraduraRepository.findById(id).orElse(null);
    }

    public String obtenerTokenPorCerradura(Cerradura Cerradura) {
        return Cerradura.getToken();
    }

    public Cerradura guardarCerradura(String ubicacion, String token, Long gestorId) {
        Cerradura cerradura = new Cerradura();
        cerradura.setUbicacion(ubicacion);
        cerradura.setToken(token);
        cerradura.setBateria(100); // Se implementará en el siguiente sprint el monitoreo de la batería
    
        // Asociar el gestor a la cerradura
        Gestor gestor = new Gestor();
        gestor.setId(gestorId); // Solo necesitas establecer el ID si no tienes el objeto completo
        cerradura.setGestor(gestor);
    
        return cerraduraRepository.save(cerradura);
    }

    public Cerradura obtenerPrimera() {
    List<Cerradura> cerraduras = obtenerTodasLasCerraduras();
    if (!cerraduras.isEmpty()) {
        return cerraduras.get(0);
    }
    throw new NoSuchElementException("No hay cerraduras disponibles.");
    }

    @Transactional
    public void eliminarCerradura(Long id) {
        try {
            Cerradura cerradura = cerraduraRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("❌ Cerradura no encontrada con ID: " + id));

            List<Reserva> reservas = reservaRepository.findByCerraduraId(id);

            for (Reserva reserva : reservas) {
                reservaService.eliminarReservaYHuespedes(reserva.getId());
            }

            reservaRepository.deleteAll(reservas); // Ahora sí, borrar las reservas
            cerraduraRepository.delete(cerradura); // Finalmente borrar la cerradura

            System.out.println("✅ Cerradura y reservas asociadas eliminadas correctamente (ID: " + id + ")");
        } catch (Exception e) {
            System.out.println("❌ Error al eliminar cerradura: " + e.getMessage());
            throw e;
        }
    }

    public void eliminarCerradurasPorGestor(Long gestorId) {
        List<Cerradura> cerraduras = obtenerCerradurasPorGestor(gestorId);
        for (Cerradura cerradura : cerraduras) {
            eliminarCerradura(cerradura.getId());
        }
    }

    public Cerradura obtenerCerraduraPorUbicacion(String ubicacion) {
            List<Cerradura> cerraduras = cerraduraRepository.findByUbicacion(ubicacion);
            return cerraduras.isEmpty() ? null : cerraduras.get(0);
        }

}
