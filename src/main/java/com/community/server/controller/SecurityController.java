package com.community.server.controller;

import com.community.server.body.SecurityBody;
import com.community.server.entity.UserEntity;
import com.community.server.repository.UserRepository;
import com.community.server.security.JwtAuthenticationFilter;
import com.community.server.security.JwtTokenProvider;
import com.community.server.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.Random;

@RestController
@RequestMapping("/api/security")
public class SecurityController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public MailService mailService;

    @Value("${app.resetExpirationInMs}")
    private int resetExpirationInMs;

    @PatchMapping("/change/password")
    public ResponseEntity<?> changePassword(HttpServletRequest request, @Valid @RequestBody SecurityBody securityBody) {
        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        if(!passwordEncoder.matches(securityBody.getOldPassword(), userEntity.getPassword()))
            return new ResponseEntity("The current password is incorrect!", HttpStatus.BAD_REQUEST);

        if(!securityBody.getNewPassword().matches("(?=^.{6,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$"))
            return new ResponseEntity("Wrong password format!", HttpStatus.BAD_REQUEST);

        userEntity.setPassword(passwordEncoder.encode(securityBody.getNewPassword()));
        userRepository.save(userEntity);
        return new ResponseEntity("Password changed!", HttpStatus.OK);
    }

    @GetMapping("/change/email")
    public ResponseEntity<?> sendCode(HttpServletRequest request) {
        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        userEntity.setEmailCode(new Random().ints(48, 122)
                .filter(i -> (i < 57 || i > 65) && (i < 90 || i > 97))
                .mapToObj(i -> (char) i)
                .limit(6)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString().toUpperCase());

        userEntity.setEmailDate(new Date(new Date().getTime() + resetExpirationInMs));

        try {
            mailService.sendEmail(userEntity.getEmail(), "Смена почты", "Ваш код - " + userEntity.getEmailCode());
        } catch (MessagingException e) {
            return new ResponseEntity("Unable to send message", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        userRepository.save(userEntity);
        return new ResponseEntity("A message with a change email code has been sent!", HttpStatus.OK);
    }

    @PatchMapping("/change/email")
    public ResponseEntity<?> changeEmail(HttpServletRequest request, @Valid @RequestBody SecurityBody securityBody) {

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        if(userEntity.getEmailCode() == null || !userEntity.getEmailCode().equalsIgnoreCase(securityBody.getCode()))
            return new ResponseEntity("Invalid code entered!", HttpStatus.BAD_REQUEST);

        if(userEntity.getEmailDate() == null || userEntity.getEmailDate().before(new Date()))
            return new ResponseEntity("Code time is up!", HttpStatus.BAD_REQUEST);

        if (userRepository.existsByEmail(securityBody.getNewEmail()))
            return new ResponseEntity("Email Address already in use!", HttpStatus.BAD_REQUEST);

        userEntity.setEmailCode(null);
        userEntity.setEmailDate(null);
        userEntity.setEmail(securityBody.getNewEmail());

        userRepository.save(userEntity);
        return new ResponseEntity("Email address has been changed", HttpStatus.OK);
    }
}
