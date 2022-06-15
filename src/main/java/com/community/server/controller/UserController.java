package com.community.server.controller;

import com.community.server.entity.FileEntity;
import com.community.server.entity.UserEntity;
import com.community.server.enums.TypeFile;
import com.community.server.dto.ChangePasswordDto;
import com.community.server.dto.SettingsDto;
import com.community.server.mapper.UserMapper;
import com.community.server.repository.FileRepository;
import com.community.server.repository.UserRepository;
import com.community.server.security.JwtAuthenticationFilter;
import com.community.server.security.JwtTokenProvider;
import com.community.server.view.UserView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/settings")
    public UserView getSettings(HttpServletRequest request) throws UnknownHostException {
        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        return userMapper.toModel(userEntity);
    }

    @PutMapping("/settings")
    public ResponseEntity<?> updateSettings(HttpServletRequest request, @Valid @RequestBody SettingsDto settingsDto){

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        if(settingsDto.getUsername() != null && !userEntity.getUsername().equals(settingsDto.getUsername())) {
            if(settingsDto.getUsername().length() < 6)
                return new ResponseEntity("The minimum username length is 6 characters!", HttpStatus.BAD_REQUEST);

            if(!settingsDto.getUsername().matches("^[a-zA-Z0-9]+$"))
                return new ResponseEntity("Invalid username format! The name must contain Latin characters and numbers!", HttpStatus.BAD_REQUEST);

            if(userRepository.existsByUsername(settingsDto.getUsername()))
                return new ResponseEntity("Username is already taken!", HttpStatus.BAD_REQUEST);

            userEntity.setUsername(settingsDto.getUsername());
        }

        if(settingsDto.getName() != null && !userEntity.getName().equals(settingsDto.getName())) {
            if(settingsDto.getName().isEmpty())
                return new ResponseEntity("Empty name!", HttpStatus.BAD_REQUEST);

            String[] divided = settingsDto.getName().split(" ");
            if(divided.length >= 3)
                return new ResponseEntity("Invalid name format! Format: First name Last name, First name", HttpStatus.BAD_REQUEST);

            for(String temp: divided) {
                if(!temp.matches("^[а-яА-ЯёЁa-zA-Z]+$"))
                    return new ResponseEntity("The name must contain only Cyrillic and Latin letters!", HttpStatus.BAD_REQUEST);
            }

            userEntity.setName(settingsDto.getName());
        }

        if(settingsDto.getShowContactEmail() != null) userEntity.setShowContactEmail(settingsDto.getShowContactEmail());
        if(settingsDto.getShowContactPhone() != null) userEntity.setShowContactPhone(settingsDto.getShowContactPhone());

        if(settingsDto.getContactEmail() != null) {
            if (!settingsDto.getContactEmail().isEmpty() && !settingsDto.getContactEmail().matches("^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$"))
                return new ResponseEntity("Invalid mailing address format!", HttpStatus.BAD_REQUEST);

            userEntity.setContactEmail(settingsDto.getContactEmail());
        }

        if(settingsDto.getContactPhone() != null) {
            if(!settingsDto.getContactPhone().isEmpty() && !settingsDto.getContactPhone().matches("^\\+[\\(\\-]?(\\d[\\(\\)\\-]?){11}\\d$"))
                return new ResponseEntity("Invalid phone format!", HttpStatus.BAD_REQUEST);

            userEntity.setContactPhone(settingsDto.getContactPhone());
        }

        if(settingsDto.getNotifyAction() != null)
            userEntity.setNotifyAction(settingsDto.getNotifyAction());

        if(settingsDto.getNotifyRequest() != null)
            userEntity.setNotifyRequest(settingsDto.getNotifyRequest());

        if(settingsDto.getNotifyMessage() != null)
            userEntity.setNotifyMessage(settingsDto.getNotifyMessage());

        if(settingsDto.getNotifyEmail() != null)
            userEntity.setNotifyEmail(settingsDto.getNotifyEmail());

        if(settingsDto.getType() != null)
            userEntity.setType(settingsDto.getType());

        if(settingsDto.getMessage() != null)
            userEntity.setMessage(settingsDto.getMessage());

        if(settingsDto.getShowMessageRequest() != null)
            userEntity.setShowMessageRequest(settingsDto.getShowMessageRequest());

        if(settingsDto.getShowMessageReaction() != null)
            userEntity.setShowMessageReaction(settingsDto.getShowMessageReaction());

        userRepository.save(userEntity);
        return new ResponseEntity("User settings updated!", HttpStatus.OK);
    }

    @PostMapping("/change/avatar")
    public ResponseEntity<?> changeAvatar(HttpServletRequest request, @RequestParam MultipartFile file) throws IOException {
        if(file.isEmpty())
            return new ResponseEntity("File is empty!", HttpStatus.BAD_REQUEST);

        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        if(!suffix.equalsIgnoreCase("jpg") && !suffix.equalsIgnoreCase("jpeg") && !suffix.equalsIgnoreCase("png"))
            return new ResponseEntity("The file is not a photo! Need png, jpeg, jpg format!", HttpStatus.BAD_REQUEST);

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        File directory = new File("resources");
        if(directory.mkdir()) logger.info("The avatar directory has been created!");

        String fileName = UUID.randomUUID().toString() + "." + suffix;
        String pathTofile = "resources/"+ fileName;
        File photo = new File(pathTofile);

        if(photo.createNewFile()) logger.info("The avatar file has been created!");

        byte[] bytes = file.getBytes();
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(pathTofile));
        stream.write(bytes);
        stream.close();

        userEntity.setFileNameAvatar(fileName);

        FileEntity fileEntity = new FileEntity(fileName, userEntity.getId(), new Date(), TypeFile.FILE_AVATAR);

        fileRepository.save(fileEntity);
        userRepository.save(userEntity);
        return new ResponseEntity("User photo changed!", HttpStatus.OK);
    }

    @PutMapping("/change/password")
    public ResponseEntity<?> changePassword(HttpServletRequest request, @Valid @RequestBody ChangePasswordDto changePasswordDto) {
        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        if(!passwordEncoder.matches(changePasswordDto.getPasswordOld(), userEntity.getPassword()))
            return new ResponseEntity("The current password is incorrect!", HttpStatus.BAD_REQUEST);

        if(!changePasswordDto.getPasswordNew().matches("(?=^.{6,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$"))
            return new ResponseEntity("Wrong password format!", HttpStatus.BAD_REQUEST);

        userEntity.setPassword(passwordEncoder.encode(changePasswordDto.getPasswordNew()));
        userRepository.save(userEntity);
        return new ResponseEntity("Password changed!", HttpStatus.OK);
    }

    @GetMapping("/find/{name}")
    public ResponseEntity<?> findUser(@PathVariable String name) throws UnknownHostException {

        List<UserEntity> users = userRepository.findByUsernameContainingIgnoreCase(name);

        if(users.size() <= 0)
            return new ResponseEntity("No user found with the specified username!", HttpStatus.BAD_REQUEST);

        /*JSONArray jsonArray = new JSONArray();
        for(int i = 0; i < users.size(); i++){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", users.get(i).getName());
            jsonObject.put("username", users.get(i).getUsername());
            jsonObject.put("avatar", InetAddress.getLocalHost().getHostAddress() + ":" + serverPort + "/resources/avatar/" + users.get(i).getFileNameAvatar());
            jsonArray.add(jsonObject);
        }*/

        return new ResponseEntity("Later...", HttpStatus.OK);
    }
}
