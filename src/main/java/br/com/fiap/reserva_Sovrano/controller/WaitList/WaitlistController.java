package br.com.fiap.reserva_Sovrano.controller.WaitList;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.reserva_Sovrano.components.Period;
import br.com.fiap.reserva_Sovrano.service.WaitlistService;

@RestController
@RequestMapping("/waitlist")
public class WaitlistController {

    @Autowired
    private WaitlistService waitlistService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestParam Long userId,
                                  @RequestParam int peopleCount,
                                  @RequestParam Period period) {

        return ResponseEntity.ok(
                waitlistService.joinWaitlist(userId, peopleCount, period)
        );
    }

    @GetMapping("/admin")
    public ResponseEntity<?> adminView(
            @RequestParam LocalDate date,
            @RequestParam Period period) {

        return ResponseEntity.ok(
                waitlistService.getWaitlist(date, period)
        );
    }
}

