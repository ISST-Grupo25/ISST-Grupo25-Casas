package com.isst.ISST_Grupo25_Casas.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.isst.ISST_Grupo25_Casas.models.User;
import com.isst.ISST_Grupo25_Casas.repository.UserRepository;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String name, String email, String password, String role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El correo ya está registrado.");
        }
    
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
    
        System.out.println("🛠 Registrando usuario: " + email);
        System.out.println("🔑 Contraseña encriptada: " + user.getPassword());
        System.out.println("🛠 Rol del usuario: " + role);
    
        return userRepository.save(user);
    }    


    public Optional<User> authenticate(String email, String password) {
        System.out.println("➡️ Intentando autenticar usuario con email: " + email);
    
        Optional<User> user = userRepository.findByEmail(email);
    
        if (user.isEmpty()) {
            System.out.println("❌ No se encontró usuario con el email: " + email);
            return Optional.empty();
        }
    
        System.out.println("🔍 Usuario encontrado: " + user.get().getEmail());
        System.out.println("🔑 Contraseña en BD (encriptada): " + user.get().getPassword());
    
        if (passwordEncoder.matches(password, user.get().getPassword())) {
            System.out.println("✅ Contraseña correcta para: " + email);
            return user;
        } else {
            System.out.println("❌ Contraseña incorrecta para: " + email);
            return Optional.empty();
        }
    }    
}
