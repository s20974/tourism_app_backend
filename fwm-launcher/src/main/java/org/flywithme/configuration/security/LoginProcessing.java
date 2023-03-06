package org.flywithme.configuration.security;

import lombok.SneakyThrows;
import org.flywithme.configuration.jwt.JwtUtil;
import org.flywithme.entity.Role;
import org.flywithme.entity.User;
import org.flywithme.exceptions.EmailNotConfirmed;
import org.flywithme.exceptions.IncorrectLoginOrPassword;
import org.flywithme.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class LoginProcessing {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;


    @SneakyThrows
    @CrossOrigin(origins = "http://localhost:3000",  allowedHeaders = "*")
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginDto login(@RequestBody User user){
        Optional<User> user1 = userRepository.findUserByEmail(user.getEmail());
            if(user1.isEmpty()){
                throw new IncorrectLoginOrPassword("Invalid username or password");
            }else if(!user1.get().isEnabled()) {
                throw new EmailNotConfirmed("Email not confirmed");
            }
            try {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));
            }catch (Exception e){
                throw new IncorrectLoginOrPassword("Invalid username or password");
            }
            var roles = user1.get().getRoles().stream().map(Role::getRoles).collect(Collectors.toSet());
            var jwt = jwtUtil.generateToken(user.getUsername());
            return LoginDto.builder()
                    .jwt(jwt)
                    .rolesSet(roles)
                    .build();
    }
}
