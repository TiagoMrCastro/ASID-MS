package MicroServices.UserService.controller;

import MicroServices.UserService.dto.*;
import MicroServices.UserService.entity.User;
import MicroServices.UserService.repository.UserRepository;
import MicroServices.UserService.security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

@RestController
@RequestMapping("/auth")
public class UserAuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder encoder;
    private final UserRepository userRepo;

    public UserAuthController(AuthenticationConfiguration authConfig, JwtUtils jwtUtils,
            PasswordEncoder encoder, UserRepository userRepo) throws Exception {
        this.authenticationManager = authConfig.getAuthenticationManager();
        this.jwtUtils = jwtUtils;
        this.encoder = encoder;
        this.userRepo = userRepo;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            User user = new User();
            user.setUsername(req.getUsername());
            user.setEmail(req.getEmail());
            user.setFullname(req.getFullname());
            user.setPassword(req.getPassword()); // SEM criptografia por enquanto
            userRepo.save(user);
            return ResponseEntity.ok(Map.of("message", "Utilizador registado com sucesso"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                     .body(Map.of("error", "Utilizador ou e-mail já existe"));

        }
    }

    @PostMapping("/login")

    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

            Optional<User> optionalUser = userRepo.findByUsername(req.getUsername());
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilizador não encontrado.");
            }

            User user = optionalUser.get();
            String token = jwtUtils.generateJwtToken(user.getId(), user.getUsername());
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
        }
    }

}
