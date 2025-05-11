package com.isst.ISST_Grupo25_Casas.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.isst.ISST_Grupo25_Casas.models.Huesped;
import com.isst.ISST_Grupo25_Casas.repository.HuespedRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class HuespedService {

    private final HuespedRepository huespedRepository;
    private final PasswordEncoder passwordEncoder;

    public HuespedService(HuespedRepository huespedRepository, PasswordEncoder passwordEncoder) {
        this.huespedRepository = huespedRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Huesped registerHuesped(String name, String email, String password) {
        if (huespedRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El correo ya está registrado.");
        }
    
        Huesped huesped = new Huesped();
        huesped.setNombre(name);
        huesped.setEmail(email);
        huesped.setPassword(passwordEncoder.encode(password));
    
        System.out.println("🛠 Registrando usuario: " + email);
        System.out.println("🔑 Contraseña encriptada: " + huesped.getPassword());
    
        return huespedRepository.save(huesped);
    }    


    public Optional<Huesped> authenticate(String email, String password) {
        System.out.println("➡️ Intentando autenticar usuario con email: " + email);
    
        Optional<Huesped> huesped = huespedRepository.findByEmail(email);
    
        if (huesped.isEmpty()) {
            System.out.println("❌ No se encontró usuario con el email: " + email);
            return Optional.empty();
        }
    
        System.out.println("🔍 Usuario encontrado: " + huesped.get().getEmail());
        System.out.println("🔑 Contraseña en BD (encriptada): " + huesped.get().getPassword());
    
        if (passwordEncoder.matches(password, huesped.get().getPassword())) {
            System.out.println("✅ Contraseña correcta para: " + email);
            return huesped;
        } else {
            System.out.println("❌ Contraseña incorrecta para: " + email);
            return Optional.empty();
        }
    }    

    public List<Huesped> obtenerTodosLosHuespedes() {
        return huespedRepository.findAll();
    }

    public List<Huesped> obtenerHuespedesPorIds(List<Long> ids) {
        return huespedRepository.findAllById(ids);
    }

    public Optional<Huesped> findByEmail(String email) {
        return huespedRepository.findByEmail(email);
    }

    public Huesped obtenerPrimero() {
    List<Huesped> huespedes = obtenerTodosLosHuespedes();
    if (!huespedes.isEmpty()) {
        return huespedes.get(0);
    }
    throw new NoSuchElementException("No hay huéspedes disponibles.");
    }

    public void eliminarHuesped(Long id) {
        Huesped huesped = huespedRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Huesped no encontrado con ID: " + id));
        huespedRepository.delete(huesped);
    }

}
