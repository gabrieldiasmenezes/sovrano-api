package br.com.fiap.reserva_Sovrano.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import br.com.fiap.reserva_Sovrano.model.Users;
import br.com.fiap.reserva_Sovrano.model.Waitlist;
import br.com.fiap.reserva_Sovrano.model.Reservations;

import java.time.Duration;

@Service
public class NotificationService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${waitlist.confirmation.timeout.minutes:15}")
    private int confirmationTimeoutMinutes;

    public void sendWaitlistPositionEmail(Users user, Waitlist entry, int position) {
        String subject = "Você entrou na fila de espera - posição " + position;
        String text = String.format(
                "Olá %s,\n\nVocê entrou na fila de espera para %s (%d pessoas).\nSua posição atual: %d.\n\nAcompanhe sua posição no aplicativo.\n\nAtenciosamente,\nSovrano",
                user.getName(), entry.getPeriod(), entry.getPeopleCount(), position
        );

        sendEmail(user.getEmail(), subject, text);
    }

    public void sendWaitlistNotifiedEmail(Users user, Waitlist entry, Reservations reservation) {
        String subject = "Mesa disponível — confirme sua reserva";

        String confirmUrl = String.format("%s/reservations/confirm?reservationId=%d", frontendUrl, reservation.getId());

        String text = String.format(
                "Olá %s,\n\nUma mesa ficou disponível para você.\nReserva temporária: id=%d, mesa=%d, para %d pessoas.\nPor favor confirme em até %d minutos: %s\n\nSe não confirmar, a vaga será liberada para o próximo na fila.\n\nAtenciosamente,\nSovrano",
                user.getName(), reservation.getId(), reservation.getTableId(), reservation.getPeopleCount(), confirmationTimeoutMinutes, confirmUrl
        );

        sendEmail(user.getEmail(), subject, text);
    }

    public void sendReservationCreatedEmail(Users user, Reservations reservation) {
        String subject = "Reserva criada com sucesso";
        String text = String.format(
                "Olá %s,\n\nSua reserva foi criada:\n- id: %d\n- data/hora: %s\n- pessoas: %d\n- mesa: %s\n- status: %s\n\nAtenciosamente,\nSovrano",
                user.getName(), reservation.getId(), reservation.getReservationDateTime(), reservation.getPeopleCount(), reservation.getTableId(), reservation.getStatus()
        );

        sendEmail(user.getEmail(), subject, text);
    }

    private void sendEmail(String to, String subject, String text) {
        if (mailSender == null) {
            // Fallback: log to console if no mail sender configured
            System.out.println("[Mail fallback] To: " + to + " Subject: " + subject + "\n" + text);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    /**
     * Send a raw test email (useful for quick admin tests).
     */
    public void sendTestEmail(String to, String subject, String body) {
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Recipient email is required");
        }
        sendEmail(to, subject, body);
    }
}
