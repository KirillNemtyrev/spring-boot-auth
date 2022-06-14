package com.community.server.controller;

import com.community.server.entity.FileEntity;
import com.community.server.entity.UserEntity;
import com.community.server.enums.CensoredMessageEntity;
import com.community.server.enums.TypeFile;
import com.community.server.enums.TypePrivateEntity;
import com.community.server.payload.ChangePasswordRequest;
import com.community.server.payload.SettingsRequest;
import com.community.server.repository.FileRepository;
import com.community.server.repository.UserRepository;
import com.community.server.security.JwtAuthenticationFilter;
import com.community.server.security.JwtTokenProvider;
import com.community.server.service.UserService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.community.server.enums.TypePrivateEntity.TYPE_PUBLIC;

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

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/settings")
    public ResponseEntity<?> getSettings(HttpServletRequest request) throws UnknownHostException {
        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        return new ResponseEntity(userEntity.toString(), HttpStatus.OK);
    }

    @PutMapping("/settings")
    public ResponseEntity<?> updateSettings(HttpServletRequest request, @Valid @RequestBody SettingsRequest settingsRequest){

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        if(settingsRequest.getUsername() != null && !userEntity.getUsername().equals(settingsRequest.getUsername())) {
            if(settingsRequest.getUsername().length() < 6)
                return new ResponseEntity("The minimum username length is 6 characters!", HttpStatus.BAD_REQUEST);

            if(!settingsRequest.getUsername().matches("^[a-zA-Z0-9]+$"))
                return new ResponseEntity("Invalid username format! The name must contain Latin characters and numbers!", HttpStatus.BAD_REQUEST);

            if(userRepository.existsByUsername(settingsRequest.getUsername()))
                return new ResponseEntity("Username is already taken!", HttpStatus.BAD_REQUEST);

            userEntity.setUsername(settingsRequest.getUsername());
        }

        if(settingsRequest.getName() != null && !userEntity.getName().equals(settingsRequest.getName())) {
            if(settingsRequest.getName().isEmpty())
                return new ResponseEntity("Empty name!", HttpStatus.BAD_REQUEST);

            String[] divided = settingsRequest.getName().split(" ");
            if(divided.length >= 3)
                return new ResponseEntity("Invalid name format! Format: First name Last name, First name", HttpStatus.BAD_REQUEST);

            for(String temp: divided) {
                if(!temp.matches("^[а-яА-ЯёЁa-zA-Z]+$"))
                    return new ResponseEntity("The name must contain only Cyrillic and Latin letters!", HttpStatus.BAD_REQUEST);
            }

            userEntity.setName(settingsRequest.getName());
        }

        if(settingsRequest.getShowEmail() != null) userEntity.setShowContactEmail(settingsRequest.getShowEmail());
        if(settingsRequest.getShowEmail() != null) userEntity.setShowContactPhone(settingsRequest.getShowEmail());

        if(settingsRequest.getEmail() != null) {
            if (!settingsRequest.getEmail().isEmpty() && !settingsRequest.getEmail().matches("^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$"))
                return new ResponseEntity("Invalid mailing address format!", HttpStatus.BAD_REQUEST);

            userEntity.setContactEmail(settingsRequest.getEmail());
        }

        if(settingsRequest.getPhone() != null) {
            if(!settingsRequest.getPhone().isEmpty() && !settingsRequest.getPhone().matches("^\\+[\\(\\-]?(\\d[\\(\\)\\-]?){11}\\d$"))
                return new ResponseEntity("Invalid phone format!", HttpStatus.BAD_REQUEST);

            userEntity.setContactPhone(settingsRequest.getPhone());
        }

        if(settingsRequest.getNotifyAction() != null)
            userEntity.setNotifyAction(settingsRequest.getNotifyAction());

        if(settingsRequest.getNotifyRequest() != null)
            userEntity.setNotifyRequest(settingsRequest.getNotifyRequest());

        if(settingsRequest.getNotifyMessage() != null)
            userEntity.setNotifyMessage(settingsRequest.getNotifyMessage());

        if(settingsRequest.getNotifyEmail() != null)
            userEntity.setNotifyEmail(settingsRequest.getNotifyEmail());

        if(settingsRequest.getType() != null) {
            if(settingsRequest.getType().equals("TYPE_PUBLIC"))
                userEntity.setType(TypePrivateEntity.TYPE_PUBLIC);

            if(settingsRequest.getType().equals("TYPE_PRIVATE"))
                userEntity.setType(TypePrivateEntity.TYPE_PRIVATE);

            if(settingsRequest.getType().equals("TYPE_CLOSE"))
                userEntity.setType(TypePrivateEntity.TYPE_CLOSE);
        }

        if(settingsRequest.getMessage() != null) {
            if(settingsRequest.getMessage().equals("NO_GET_MESSAGE"))
                userEntity.setMessage(CensoredMessageEntity.NO_GET_MESSAGE);

            if(settingsRequest.getMessage().equals("CENSORED_MESSAGE"))
                userEntity.setMessage(CensoredMessageEntity.CENSORED_MESSAGE);

            if(settingsRequest.getMessage().equals("NO_CENSORED"))
                userEntity.setMessage(CensoredMessageEntity.NO_CENSORED);
        }

        if(settingsRequest.getShowMessageRequest() != null)
            userEntity.setShowMessageRequest(settingsRequest.getShowMessageRequest());

        if(settingsRequest.getShowMessageReaction() != null)
            userEntity.setShowMessageReaction(settingsRequest.getShowMessageReaction());

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
    public ResponseEntity<?> changePassword(HttpServletRequest request, @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        if(!passwordEncoder.matches(changePasswordRequest.getPasswordOld(), userEntity.getPassword()))
            return new ResponseEntity("The current password is incorrect!", HttpStatus.BAD_REQUEST);

        if(!changePasswordRequest.getPasswordNew().matches("(?=^.{6,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$"))
            return new ResponseEntity("Wrong password format!", HttpStatus.BAD_REQUEST);

        userEntity.setPassword(passwordEncoder.encode(changePasswordRequest.getPasswordNew()));
        userRepository.save(userEntity);
        return new ResponseEntity("Password changed!", HttpStatus.OK);
    }

    @GetMapping("/find/{name}")
    public ResponseEntity<?> findUser(@PathVariable String name) throws UnknownHostException {

        List<UserEntity> users = userRepository.findByUsernameContainingIgnoreCase(name);

        if(users.size() <= 0)
            return new ResponseEntity("No user found with the specified username!", HttpStatus.BAD_REQUEST);

        JSONArray jsonArray = new JSONArray();
        for(int i = 0; i < users.size(); i++){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", users.get(i).getName());
            jsonObject.put("username", users.get(i).getUsername());
            jsonObject.put("avatar", InetAddress.getLocalHost().getHostAddress() + ":" + serverPort + "/resources/avatar/" + users.get(i).getFileNameAvatar());
            jsonArray.add(jsonObject);
        }

        return new ResponseEntity(jsonArray.toJSONString(), HttpStatus.OK);
    }
}
