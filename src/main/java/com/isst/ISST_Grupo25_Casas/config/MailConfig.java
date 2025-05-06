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

        // Configuración principal
        mailSender.setHost(env.getProperty("spring.mail.host", "smtp.gmail.com"));
        mailSender.setPort(env.getProperty("spring.mail.port", Integer.class, 587));
        mailSender.setUsername(env.getProperty("spring.mail.username"));
        mailSender.setPassword(env.getProperty("spring.mail.password"));
        mailSender.setProtocol("smtp");

        // Propiedades adicionales de SMTP
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        safePut(props, "mail.smtp.auth", env.getProperty("spring.mail.properties.mail.smtp.auth", "true"));
        safePut(props, "mail.smtp.starttls.enable", env.getProperty("spring.mail.properties.mail.smtp.starttls.enable", "true"));
        safePut(props, "mail.smtp.starttls.required", env.getProperty("spring.mail.properties.mail.smtp.starttls.required", "true"));

        return mailSender;
    }

    private void safePut(Properties props, String key, String value) {
        if (value != null) {
            props.put(key, value);
        }
    }
}
