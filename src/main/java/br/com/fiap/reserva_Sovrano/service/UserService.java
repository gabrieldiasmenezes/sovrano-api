package br.com.fiap.reserva_Sovrano.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.fiap.reserva_Sovrano.components.PriorityType;
import br.com.fiap.reserva_Sovrano.components.UserRole;
import br.com.fiap.reserva_Sovrano.model.Users;
import br.com.fiap.reserva_Sovrano.model.dto.UserPasswordUpdateDto;
import br.com.fiap.reserva_Sovrano.model.dto.UserUpdateDto;
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
        user.setVipLevel(user.getVipLevel() == PriorityType.LEGAL ? PriorityType.LEGAL : PriorityType.NONE);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.CUSTOMER);
        return userRepository.save(user);
    }

    public Users createAdmin(Users user){
        GlobalUtils.check(userRepository.existsByEmail(user.getEmail()),
            "Email já registrado."
        );

        initializeUserFields(user);
        user.setVisitsCount(0);
        user.setVipLevel(user.getVipLevel() == PriorityType.LEGAL ? PriorityType.LEGAL : PriorityType.NONE);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.ADMIN);
        return userRepository.save(user);
    }
    public Users update(String email, UserUpdateDto dto) {
        Users user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // Atualiza apenas campos permitidos
        if (dto.name() != null) {
            user.setName(dto.name());
        }

        if (dto.phone() != null) {
            user.setPhone(dto.phone());
        }

        // VIP SEMPRE recalculado pelo sistema
        updateVipLevel(user);

        return userRepository.save(user);
    }

    public void updatePassword(String email, UserPasswordUpdateDto dto) {
        Users user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // Confere senha atual
        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        // Evita reutilizar a mesma senha
        if (passwordEncoder.matches(dto.newPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from the current one.");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
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
            user.setVipLevel(PriorityType.VIP_3);
        } else if (user.getVisitsCount() >= 5) {
            user.setVipLevel(PriorityType.VIP_2);
        } else if (user.getVisitsCount() >= 3) {
            user.setVipLevel(PriorityType.VIP_1);
        } else if (user.getVipLevel() != PriorityType.LEGAL) {
            user.setVipLevel(PriorityType.NONE);
        }
    }
}
