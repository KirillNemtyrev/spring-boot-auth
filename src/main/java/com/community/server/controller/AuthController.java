package com.community.server.controller;

import com.community.server.entity.*;
import com.community.server.enums.CensoredMessageEntity;
import com.community.server.enums.RoleNameEntity;
import com.community.server.enums.TypePrivateEntity;
import com.community.server.exception.AppException;
import com.community.server.payload.*;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    public AuthenticationManager authenticationManager;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public RoleRepository roleRepository;

    @Autowired
    public SupportRepository supportRepository;

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Autowired
    public JwtTokenProvider tokenProvider;

    @Autowired
    public UserService userService;

    @Autowired
    public MailService mailService;

    @Value("${app.resetExpirationInMs}")
    private int resetExpirationInMs;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(!signUpRequest.getUsername().matches("^[a-zA-Z0-9]+$"))
            return new ResponseEntity("Invalid username!", HttpStatus.BAD_REQUEST);

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity("Email Address already in use!", HttpStatus.BAD_REQUEST);
        }

        if(!signUpRequest.getPassword().matches("(?=^.{6,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$"))
            return new ResponseEntity("Wrong password format!", HttpStatus.BAD_REQUEST);

        UserEntity user = new UserEntity(signUpRequest.getUsername(), signUpRequest.getUsername(), signUpRequest.getEmail(),
                signUpRequest.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        RoleEntity userRole = roleRepository.findByName(RoleNameEntity.ROLE_USER)
                .orElseThrow(() -> new AppException("User Role not set."));

        user.setRoles(Collections.singleton(userRole));
        user.setResetcode("null");
        user.setCreate_time(new Date());
        user.setPrivate_status(TypePrivateEntity.TYPE_PUBLIC);
        user.setCensoredMessage(CensoredMessageEntity.NO_GET_MESSAGE);
        user.setPath_avatar("no_avatar.jpg");

        user.setShow_email(false);
        user.setShow_phone(false);

        user.setNotification_action(true);
        user.setNotification_message(true);
        user.setNotification_request(true);
        user.setNotification_email(true);
        user.setShow_message_request(true);
        user.setShow_message_reaction(true);

        userRepository.save(user);
        return new ResponseEntity("User registered successfully", HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/reset/send")
    public ResponseEntity<?> recoverycodeUser(@Valid @RequestBody RecoverySendRequest recoveryRequest) {

        UserEntity user = userRepository.findByEmail(recoveryRequest.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("User with given email address not found!"));


        user.setResetcode(new Random().ints(48, 122)
                .filter(i -> (i < 57 || i > 65) && (i < 90 || i > 97))
                .mapToObj(i -> (char) i)
                .limit(6)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString().toUpperCase());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + resetExpirationInMs);
        user.setReset_time(expiryDate);

        try {
            mailService.sendEmail(recoveryRequest.getEmail(), "Восстановление учётной записи", "Ваш код - " + user.getResetcode());
        } catch (MessagingException e) {
            return new ResponseEntity("Unable to send message", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        userRepository.save(user);
        return new ResponseEntity("A message with a recovery code has been sent!", HttpStatus.OK);
    }

    @PostMapping("/reset/change")
    public ResponseEntity<?> recoverypasswordUser(@Valid @RequestBody RecoveryChangeRequest recoveryRequest) {

        UserEntity user = userRepository.findByEmail(recoveryRequest.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("User with given email address not found!"));

        if(user.getResetcode().equals("null") || !user.getResetcode().equalsIgnoreCase(recoveryRequest.getCode())) {
            return new ResponseEntity("Invalid code entered!", HttpStatus.BAD_REQUEST);
        }

        if(!recoveryRequest.getNew_password().matches("(?=^.{6,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$"))
            return new ResponseEntity("Wrong password format!", HttpStatus.BAD_REQUEST);

        user.setPassword(passwordEncoder.encode(recoveryRequest.getNew_password()));
        user.setResetcode("null");

        userRepository.save(user);
        return new ResponseEntity("A message with a recovery code has been sent!", HttpStatus.OK);
    }

    @PostMapping("/support")
    public ResponseEntity<?> support(@Valid @RequestBody SupportRequest supportRequest) {

        SupportEntity supportEntity = new SupportEntity(
                supportRequest.getEmail(), supportRequest.getTitle(), supportRequest.getMessage());

        supportRepository.save(supportEntity);
        return new ResponseEntity("Support email sent!", HttpStatus.OK);
    }
}
