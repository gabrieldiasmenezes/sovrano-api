package br.com.fiap.reserva_Sovrano.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Users update(String email, Users user){
            Users existingUser = GlobalUtils.getOrThrow(
                userRepository.findByEmail(email),
                "User not found."
            );
        existingUser.setName(user.getName());
        existingUser.setPhone(user.getPhone());
        return userRepository.save(existingUser);
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
}
