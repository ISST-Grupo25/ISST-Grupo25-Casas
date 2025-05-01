package com.isst.ISST_Grupo25_Casas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

  @Bean
  public JavaMailSender javaMailSender(Environment env) {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

    // Ajusta manualmente TODO lo necesario:
    mailSender.setProtocol("smtp");                                // ← fuerza SMTP
    mailSender.setHost(env.getProperty("spring.mail.host"));
    mailSender.setPort(env.getProperty("spring.mail.port", Integer.class, 587));
    mailSender.setUsername(env.getProperty("spring.mail.username"));
    mailSender.setPassword(env.getProperty("spring.mail.password"));

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");                  // ← fuerza SMTP aquí también
    props.put("mail.smtp.auth",    env.getProperty("spring.mail.properties.mail.smtp.auth"));
    props.put("mail.smtp.starttls.enable",  env.getProperty("spring.mail.properties.mail.smtp.starttls.enable"));
    props.put("mail.smtp.starttls.required", env.getProperty("spring.mail.properties.mail.smtp.starttls.required"));

    return mailSender;
  }
}
