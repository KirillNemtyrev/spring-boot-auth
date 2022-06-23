package com.community.server.controller;

import com.community.server.body.*;
import com.community.server.entity.*;
import com.community.server.entity.RoleNameEntity;
import com.community.server.exception.AppException;
import com.community.server.repository.RoleRepository;
import com.community.server.repository.SupportRepository;
import com.community.server.repository.UserRepository;
import com.community.server.security.JwtAuthenticationResponse;
import com.community.server.security.JwtTokenProvider;
import com.community.server.service.MailService;
import com.community.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;

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
    public PasswordEncoder passwordEncoder;

    @Autowired
    public JwtTokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(HttpServletRequest request, @Valid @RequestBody SignUP signUP) {

        if(!signUP.getUsername().matches("^[a-zA-Z0-9]+$"))
            return new ResponseEntity("Invalid username!", HttpStatus.BAD_REQUEST);

        if (userRepository.existsByUsername(signUP.getUsername()))
            return new ResponseEntity("Username is already taken!", HttpStatus.BAD_REQUEST);

        if (userRepository.existsByEmail(signUP.getEmail()))
            return new ResponseEntity("Email Address already in use!", HttpStatus.BAD_REQUEST);

        if(!signUP.getPassword().matches("(?=^.{6,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$"))
            return new ResponseEntity("Wrong password format!", HttpStatus.BAD_REQUEST);

        UserEntity userEntity = new UserEntity(
                signUP.getName(), signUP.getUsername(), signUP.getEmail(), passwordEncoder.encode(signUP.getPassword()));

        RoleEntity roleEntity = roleRepository.findByName(RoleNameEntity.ROLE_USER).orElseThrow(
                () -> new AppException("User Role not set."));

        userEntity.setRoles(Collections.singleton(roleEntity));
        userEntity.setRegisterIP(request.getRemoteAddr());

        userRepository.save(userEntity);
        return new ResponseEntity("User registered successfully", HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SignIN signIN) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signIN.getEmail(), signIN.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }
}
