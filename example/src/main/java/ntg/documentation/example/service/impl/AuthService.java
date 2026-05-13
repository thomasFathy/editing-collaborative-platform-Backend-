package ntg.documentation.example.service.impl;

import lombok.RequiredArgsConstructor;
import ntg.documentation.example.domain.dto.AuthResponse;
import ntg.documentation.example.domain.dto.LoginRequest;
import ntg.documentation.example.domain.dto.RegisterRequest;
import ntg.documentation.example.domain.entity.User;
import ntg.documentation.example.exception.EmailAlreadyExistsException;
import ntg.documentation.example.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public AuthResponse register(RegisterRequest request){

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("This email address is already registered.");
        }
        User user= User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);

        String token= jwtService.generateToken(user.getId());
        return new AuthResponse(token);
    }


    public AuthResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException(""));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Password doesn't match");
        }

        String token = jwtService.generateToken(user.getId());

        return new AuthResponse(token);
    }
}
