package com.community.server.controller;

import com.community.server.body.Settings;
import com.community.server.dto.UserSettings;
import com.community.server.entity.UserEntity;
import com.community.server.mapper.SettingsUserMapper;
import com.community.server.repository.UserRepository;
import com.community.server.security.JwtAuthenticationFilter;
import com.community.server.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SettingsUserMapper settingsUserMapper;

    @GetMapping()
    public UserSettings getSettings(HttpServletRequest request) {
        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        return new UserSettings(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getUsername(),
                userEntity.getUuid(),
                userEntity.getEmail(),
                userEntity.getAboutMe(),
                userEntity.getFileNameAvatar(),
                userEntity.getCreateDate());
    }

    @PatchMapping("/username")
    public ResponseEntity<?> patchUsername(HttpServletRequest request, @Valid @RequestBody Settings settings) {

        if(!settings.getUsername().matches("^[a-zA-Z0-9]+$"))
            return new ResponseEntity("Invalid username!", HttpStatus.BAD_REQUEST);

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        if(userRepository.existsByUsername(settings.getUsername()))
            return new ResponseEntity("This username is taken!", HttpStatus.BAD_REQUEST);

        userEntity.setUsername(settings.getUsername());
        userEntity.setLastModifyDate(new Date());

        userRepository.save(userEntity);
        return new ResponseEntity("The 'username' field has been updated!", HttpStatus.OK);
    }

    @PatchMapping("/name")
    public ResponseEntity<?> patchName(HttpServletRequest request, @Valid @RequestBody Settings settings) {

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        userEntity.setName(settings.getName());
        userEntity.setLastModifyDate(new Date());

        userRepository.save(userEntity);
        return new ResponseEntity("The 'name' field has been updated!", HttpStatus.OK);
    }

    @PatchMapping("/aboutme")
    public ResponseEntity<?> patchAboutMe(HttpServletRequest request, @Valid @RequestBody Settings settings) {

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        userEntity.setAboutMe(settings.getAboutMe());
        userEntity.setLastModifyDate(new Date());

        userRepository.save(userEntity);
        return new ResponseEntity("The 'aboutMe' field has been updated!", HttpStatus.OK);
    }
}
