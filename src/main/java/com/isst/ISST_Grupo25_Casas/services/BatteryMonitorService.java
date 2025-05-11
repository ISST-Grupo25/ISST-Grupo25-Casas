package com.isst.ISST_Grupo25_Casas.services;

import com.isst.ISST_Grupo25_Casas.models.Cerradura;
import com.isst.ISST_Grupo25_Casas.repository.CerraduraRepository;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class BatteryMonitorService {

    private final CerraduraRepository cerraduraRepository;
    private Socket socket;
    private boolean notificado = false;

    @Autowired
    private JavaMailSender mailSender;


    public BatteryMonitorService(CerraduraRepository cerraduraRepository) {
        this.cerraduraRepository = cerraduraRepository;
    }

    @PostConstruct
    public void init() {
        try {
            IO.Options options = new IO.Options();
            options.forceNew = true;
            options.reconnection = true;
            options.reconnectionAttempts = Integer.MAX_VALUE;
            options.reconnectionDelay = 5000;
            options.transports = new String[] {"websocket"};

            socket = IO.socket("http://localhost:3555", options);

            socket.on(Socket.EVENT_CONNECT, args -> System.out.println("✅ Conectado al WebSocket"));
            socket.on(Socket.EVENT_CONNECT_ERROR, args -> System.out.println("❌ Error de conexión al WebSocket: " + Arrays.toString(args)));

            socket.on("batteryLevel", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length > 0) {
                        Object raw = args[0];
                        System.out.println("📦 batteryLevel recibido: " + raw);
                        try {
                            JSONObject json = new JSONObject(raw.toString());
                            int valor = json.getInt("nivel");
                            String tokenStr = json.getString("token");
                            System.out.println("🔋 Nivel batería recibido: " + valor + "% para token: " + tokenStr);
                            actualizarBateriaEnBD(tokenStr, valor);

                            if (valor <= 15) {
                                if (!notificado) {
                                    enviarNotificacion(tokenStr, valor);
                                    notificado = true;
                                }
                                if (valor == 0) {
                                    bloquearCerradura(tokenStr);
                                }
                            } else {
                                notificado = false;
                            }
                        } catch (Exception e) {
                            System.out.println("⚠️ Error procesando batteryLevel: " + e.getMessage());
                        }
                    }
                }
            });

            socket.connect();

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!socket.connected()) {
                        System.out.println("🔁 Reintentando conexión al WebSocket...");
                        socket.connect();
                    }
                }
            }, 5000, 5000);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void verificarBateria() {
        if (socket != null && socket.connected()) {
            socket.emit("getBatteryLevel");
        }
    }

    public void forzarConsultaPorToken(String token) {
        if (socket != null && socket.connected()) {
            System.out.println("📡 Enviando getBatteryLevel con token: " + token);
            socket.emit("getBatteryLevel", new JSONObject().put("token", token));
        } else {
            System.out.println("❌ WebSocket no conectado");
        }
    }

    private void actualizarBateriaEnBD(String token, int nivel) {
        try {
            Cerradura cerradura = cerraduraRepository.findAll().stream()
                .filter(c -> token.equals(c.getToken()))
                .findFirst()
                .orElse(null);

            if (cerradura != null) {
                cerradura.setBateria(nivel);
                cerraduraRepository.save(cerradura);
                System.out.println("✅ Nivel de batería actualizado en cerradura ID " + cerradura.getId());
            } else {
                System.out.println("⚠️ Cerradura no encontrada con token: " + token);
            }
        } catch (Exception e) {
            System.out.println("❌ Error al actualizar batería en BD: " + e.getMessage());
        }
    }

    private void enviarNotificacion(String token, int nivel) {
        Cerradura cerradura = cerraduraRepository.findAll().stream()
                .filter(c -> token.equals(c.getToken()))
                .findFirst()
                .orElse(null);

        if (cerradura != null && cerradura.getGestor() != null) {
            String email = cerradura.getGestor().getEmail();
            if (email != null && !email.isEmpty()) {
                SimpleMailMessage mensaje = new SimpleMailMessage();
                mensaje.setTo(email);
                mensaje.setSubject("⚠️ Batería baja en tu cerradura");
                mensaje.setText("La cerradura en " + cerradura.getUbicacion() +
                        " tiene una batería baja (" + nivel + "%). Por favor, cámbiala lo antes posible.");

                try {
                    mailSender.send(mensaje);
                    System.out.println("✅ Email enviado a " + email);
                } catch (Exception e) {
                    System.out.println("❌ Error al enviar email: " + e.getMessage());
                }
            }
        }
    }

    private void bloquearCerradura(String token) {
        System.out.println("❌ Batería agotada en cerradura (" + token + "). Cerradura bloqueada digitalmente. Requiere intervención física.");
    }
    
}
