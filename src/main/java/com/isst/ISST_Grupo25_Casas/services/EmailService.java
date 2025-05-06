package com.isst.ISST_Grupo25_Casas.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void enviarCorreoDeAcceso(String destinatario, String ubicacion, String horario) {
        System.out.println(">> PIN válido: enviando correo a " + destinatario + ", " + ubicacion + ", " + horario);
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom("internetdelascasas.g25@gmail.com");
        mensaje.setTo(destinatario);
        mensaje.setSubject("Acceso autorizado a la cerradura");
        mensaje.setText("Se ha accedido correctamente a la cerradura ubicada en: " + ubicacion
                + "\nFecha y hora del acceso: " + horario
                + "\n\nEste correo es solo informativo.");
        mailSender.send(mensaje);
    }
}
