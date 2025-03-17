package com.isst.ISST_Grupo25_Casas.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.isst.ISST_Grupo25_Casas.repository.GestorRepository;
import com.isst.ISST_Grupo25_Casas.repository.HuespedRepository;
import com.isst.ISST_Grupo25_Casas.models.Gestor;
import com.isst.ISST_Grupo25_Casas.models.Huesped;

import jakarta.servlet.http.HttpSession;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;


@Controller
public class ProfileController {

    private static final String UPLOAD_DIR = "src/main/resources/static/images/uploads";

    @Autowired
    private GestorRepository gestorRepository;
    private HuespedRepository huespedRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/profile/upload-avatar")
    public String uploadAvatar(@RequestParam("avatar") MultipartFile file, HttpSession session, Model model) {
        if (!file.isEmpty()) {
            try {
                // Guardar el archivo en la carpeta de imágenes
                Path uploadPath = Path.of(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = session.getAttribute("usuario") + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Guardar la ruta de la imagen en la sesión
                String imageUrl = "/images/uploads/" + fileName;
                session.setAttribute("avatar", imageUrl);
                model.addAttribute("avatar", imageUrl);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "redirect:/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("name") String name,
                                @RequestParam("email") String email,
                                HttpSession session, Model model) {
        String currentEmail = (String) session.getAttribute("email");

        Optional<Huesped> optionalHuesped = huespedRepository.findByEmail(currentEmail);
        Optional<Gestor> optionalGestor = gestorRepository.findByEmail(currentEmail);

        if (optionalHuesped.isPresent()) {  // 🔥 Verificar si el usuario existe
            Huesped huesped = optionalHuesped.get();  // ✅ Obtener el objeto Huesped real
            huesped.setName(name);
            huesped.setEmail(email);
            huespedRepository.save(huesped);  // 🔥 Guardar cambios en la BD

            // Actualizar la sesión con los nuevos datos
            session.setAttribute("usuario", name);
            session.setAttribute("email", email);
        }
        if (optionalGestor.isPresent()) {  // 🔥 Verificar si el usuario existe
            Gestor gestor = optionalGestor.get();  // ✅ Obtener el objeto Huesped real
            gestor.setName(name);
            gestor.setEmail(email);
            gestorRepository.save(gestor);  // 🔥 Guardar cambios en la BD

            // Actualizar la sesión con los nuevos datos
            session.setAttribute("usuario", name);
            session.setAttribute("email", email);
        }

        return "redirect:/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 HttpSession session) {
        String email = (String) session.getAttribute("email");
        Optional<Huesped> optionalHuesped = huespedRepository.findByEmail(email);
        Optional<Gestor> optionalGestor = gestorRepository.findByEmail(email);

        if (optionalHuesped.isPresent()) {
            Huesped huesped = optionalHuesped.get();

            // Verificar que la contraseña actual es correcta
            if (!passwordEncoder.matches(oldPassword, huesped.getPassword())) {
                session.setAttribute("error", "La contraseña actual es incorrecta.");
                return "redirect:/profile";
            }

            // Encriptar la nueva contraseña y guardarla
            huesped.setPassword(passwordEncoder.encode(newPassword));
            huespedRepository.save(huesped);
            session.setAttribute("message", "Contraseña actualizada con éxito.");
        } if(optionalGestor.isPresent()) {
            Gestor gestor = optionalGestor.get();

            // Verificar que la contraseña actual es correcta
            if (!passwordEncoder.matches(oldPassword, gestor.getPassword())) {
                session.setAttribute("error", "La contraseña actual es incorrecta.");
                return "redirect:/profile";
            }

            // Encriptar la nueva contraseña y guardarla
            gestor.setPassword(passwordEncoder.encode(newPassword));
            gestorRepository.save(gestor);
            session.setAttribute("message", "Contraseña actualizada con éxito.");
        } else {
            session.setAttribute("error", "Usuario no encontrado.");
        }

        return "redirect:/profile";
    }
}
