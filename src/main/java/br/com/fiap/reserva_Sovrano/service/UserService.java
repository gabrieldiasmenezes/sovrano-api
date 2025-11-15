package br.com.fiap.reserva_Sovrano.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.fiap.reserva_Sovrano.components.UserRole;
import br.com.fiap.reserva_Sovrano.model.Users;
import br.com.fiap.reserva_Sovrano.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<Users> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public Users create(Users user){
        if(userRepository.existsByEmail(user.getEmail())){
            throw new IllegalArgumentException("Email já registrado.");
        }

        user.setBlockedUntil(null);
        user.setNoShowCount(0);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Users update(String email, Users user){
        Users existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        existingUser.setName(user.getName());
        existingUser.setPhone(user.getPhone());
        return userRepository.save(existingUser);
    }

    public void delete(String email) {
        Users existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        userRepository.delete(existingUser);
    }

    public void desblockUser(Users user){
        user.setRole(UserRole.CUSTOMER);
        user.setBlockedUntil(null);
        user.setNoShowCount(0);

        userRepository.save(user);
    }
}
