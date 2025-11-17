package br.com.fiap.reserva_Sovrano.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.fiap.reserva_Sovrano.components.PriorityType;
import br.com.fiap.reserva_Sovrano.components.UserRole;
import br.com.fiap.reserva_Sovrano.model.Users;
import br.com.fiap.reserva_Sovrano.repository.UserRepository;
import br.com.fiap.reserva_Sovrano.utils.GlobalUtils;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    
    private void initializeUserFields(Users user) {
        user.setBlockedUntil(null);
        user.setNoShowCount(0);
    }

    public Optional<Users> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public Users create(Users user){
        GlobalUtils.check(userRepository.existsByEmail(user.getEmail()),
            "Email já registrado."
        );

        initializeUserFields(user);
        user.setVisitsCount(0);
        user.setPriorityType(user.getPriorityType() == PriorityType.LEGAL ? PriorityType.LEGAL : PriorityType.NONE);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Users update(String email, Users updated){
        Users user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found."));

        user.setName(updated.getName());
        user.setPhone(updated.getPhone());
        user.setPhotoUrl(updated.getPhotoUrl());

        // Prioridade legal informada pelo usuário
        if (updated.getPriorityType() == PriorityType.LEGAL) {
            user.setPriorityType(PriorityType.LEGAL);
            user.setPriorityReason(updated.getPriorityReason());
        }

        // VIP recalculado automaticamente
        updateVipLevel(user);

        return userRepository.save(user);
    }

    public void delete(String email) {
        Users existingUser= GlobalUtils.getOrThrow(
            userRepository.findByEmail(email),
            "User not found."
        );
        userRepository.delete(existingUser);
    }

    public void desblockUser(Users user){
        user.setRole(UserRole.CUSTOMER);
        initializeUserFields(user);

        userRepository.save(user);
    }

    private void updateVipLevel(Users user) {
        if (user.getVisitsCount() >= 8) {
            user.setPriorityType(PriorityType.VIP_3);
        } else if (user.getVisitsCount() >= 5) {
            user.setPriorityType(PriorityType.VIP_2);
        } else if (user.getVisitsCount() >= 3) {
            user.setPriorityType(PriorityType.VIP_1);
        } else if (user.getPriorityType() != PriorityType.LEGAL) {
            user.setPriorityType(PriorityType.NONE);
        }
    }
}
