package com.community.server.controller;

import com.community.server.body.Recovery;
import com.community.server.entity.UserEntity;
import com.community.server.repository.RoleRepository;
import com.community.server.repository.SupportRepository;
import com.community.server.repository.UserRepository;
import com.community.server.security.JwtTokenProvider;
import com.community.server.service.MailService;
import com.community.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.Date;
import java.util.Random;

@RestController
@RequestMapping("/api/recovery")
public class RecoveryController {


    @Autowired
    public UserRepository userRepository;

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Autowired
    public MailService mailService;

    @Value("${app.resetExpirationInMs}")
    private int resetExpirationInMs;

    @GetMapping("/password")
    public ResponseEntity<?> getCodeForPassword(@Valid @RequestBody Recovery recovery) {

        UserEntity userEntity = userRepository.findByEmail(recovery.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("User with given email address not found!"));

        userEntity.setRecoveryCode(new Random().ints(48, 122)
                .filter(i -> (i < 57 || i > 65) && (i < 90 || i > 97))
                .mapToObj(i -> (char) i)
                .limit(6)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString().toUpperCase());

        userEntity.setRecoveryDate(new Date(new Date().getTime() + resetExpirationInMs));

        try {
            mailService.sendEmail(recovery.getEmail(), "Восстановление учётной записи", "Ваш код - " + userEntity.getRecoveryCode());
        } catch (MessagingException e) {
            return new ResponseEntity("Unable to send message", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        userRepository.save(userEntity);
        return new ResponseEntity("A message with a recovery code has been sent!", HttpStatus.OK);
    }

    @PatchMapping("/password")
    public ResponseEntity<?> recoveryPassword(@Valid @RequestBody Recovery recovery) {

        UserEntity userEntity = userRepository.findByEmail(recovery.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("User with given email address not found!"));

        if(userEntity.getRecoveryCode() == null || !userEntity.getRecoveryCode().equalsIgnoreCase(recovery.getCode()))
            return new ResponseEntity("Invalid code entered!", HttpStatus.BAD_REQUEST);

        if(userEntity.getRecoveryDate() == null || userEntity.getRecoveryDate().before(new Date()))
            return new ResponseEntity("Code time is up!", HttpStatus.BAD_REQUEST);

        if(!recovery.getPassword().matches("(?=^.{6,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$"))
            return new ResponseEntity("Wrong password format!", HttpStatus.BAD_REQUEST);

        userEntity.setRecoveryCode(null);
        userEntity.setRecoveryDate(null);
        userEntity.setPassword(passwordEncoder.encode(recovery.getPassword()));

        userRepository.save(userEntity);
        return new ResponseEntity("A message with a recovery code has been sent!", HttpStatus.OK);
    }
}
