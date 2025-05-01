package com.isst.ISST_Grupo25_Casas.services;

import com.isst.ISST_Grupo25_Casas.models.Gestor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import jakarta.mail.MessagingException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  private final JavaMailSender mailSender;

  // inyecta tu usuario SMTP como “from”
  @Value("${spring.mail.username}")
  private String fromAddress;

  @Autowired
  public EmailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void sendLockNotification(Gestor gestor,
                                   String ubicacion,
                                   java.time.LocalDateTime when,
                                   String usuario) {
    System.out.println(">> EmailService.sendLockNotification() llamado");

    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setFrom(fromAddress);
    msg.setTo(gestor.getEmail());
    msg.setSubject("Tu cerradura “" + ubicacion + "” se ha abierto");
    msg.setText(
      "Hola " + gestor.getNombre() + ",\n\n" +
      "La cerradura “" + ubicacion + "” se ha abierto correctamente.\n" +
      "Hora: " + when + "\n" +
      "Usuario: " + usuario + "\n\n" +
      "— Tu aplicación IoH"
    );

    // aquí es donde debe compilar sin error:
    mailSender.send(msg);

    System.out.println(">> Correo enviado a " + gestor.getEmail());
  }
}
