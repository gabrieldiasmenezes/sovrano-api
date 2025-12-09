package br.com.fiap.reserva_Sovrano.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.reserva_Sovrano.service.NotificationService;

@RestController
@RequestMapping("/admin")
public class TestEmailController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/test-email")
    public ResponseEntity<String> sendTestEmail(@RequestParam String email, @RequestParam(required = false) String name) {
        String subject = "Teste de envio - Sovrano";
        String body = String.format("Olá %s,\n\nEste é um e-mail de teste enviado pelo servidor Sovrano.\n\nAtenciosamente,\nSovrano", (name == null || name.isBlank()) ? "cliente" : name);

        notificationService.sendTestEmail(email, subject, body);
        return ResponseEntity.ok("Enviado (ou log impresso em fallback). Destinatário: " + email);
    }
}
