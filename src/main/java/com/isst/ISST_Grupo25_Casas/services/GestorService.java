package com.isst.ISST_Grupo25_Casas.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.isst.ISST_Grupo25_Casas.models.Gestor;
import com.isst.ISST_Grupo25_Casas.repository.GestorRepository;
import com.isst.ISST_Grupo25_Casas.repository.GestorRepository;
import java.util.Optional;

@Service
public class GestorService {

    
    private final GestorRepository gestorRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReservaService reservaService;
    private final CerraduraService cerraduraService;

    public GestorService(GestorRepository gestorRepository, PasswordEncoder passwordEncoder, ReservaService reservaService, CerraduraService cerraduraService) {
        this.cerraduraService = cerraduraService;
        this.reservaService = reservaService;
        this.gestorRepository = gestorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Gestor registerGestor(String name, String email, String password, String phone) {
        if (gestorRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El correo ya está registrado.");
        }
    
        Gestor gestor = new Gestor();
        gestor.setNombre(name);
        gestor.setEmail(email);
        gestor.setTelefono(phone);
        gestor.setPassword(passwordEncoder.encode(password));
    
        System.out.println("🛠 Registrando usuario: " + email);
        System.out.println("🔑 Contraseña encriptada: " + gestor.getPassword());
    
        return gestorRepository.save(gestor);
    }    


    public Optional<Gestor> authenticate(String email, String password) {
        System.out.println("➡️ Intentando autenticar usuario con email: " + email);
    
        Optional<Gestor> gestor = gestorRepository.findByEmail(email);
    
        if (gestor.isEmpty()) {
            System.out.println("❌ No se encontró usuario con el email: " + email);
            return Optional.empty();
        }
    
        System.out.println("🔍 Usuario encontrado: " + gestor.get().getEmail());
        System.out.println("🔑 Contraseña en BD (encriptada): " + gestor.get().getPassword());
    
        if (passwordEncoder.matches(password, gestor.get().getPassword())) {
            System.out.println("✅ Contraseña correcta para: " + email);
            return gestor;
        } else {
            System.out.println("❌ Contraseña incorrecta para: " + email);
            return Optional.empty();
        }
    }

    public Gestor obtenerGestorPorId(Long id) {
        return gestorRepository.findById(id).orElse(null);  // Devuelve el gestor o null si no lo encuentra
    }    

    public Optional<Gestor> findByEmail(String email) {
        return gestorRepository.findByEmail(email);
    }

    public void eliminarGestor(Long id) {
        gestorRepository.deleteById(id);
        //eliminar las reservas y cerraduras asociadas a este gestor
        reservaService.eliminarReservasPorGestor(id);
        cerraduraService.eliminarCerradurasPorGestor(id);
    }
}
