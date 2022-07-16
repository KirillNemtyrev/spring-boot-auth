package com.community.server.controller;

import com.community.server.entity.FileEntity;
import com.community.server.entity.UserEntity;
import com.community.server.enums.TypeFile;
import com.community.server.repository.FileRepository;
import com.community.server.repository.UserRepository;
import com.community.server.security.JwtAuthenticationFilter;
import com.community.server.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/avatar")
public class AvatarController {

    private static final Logger logger = LoggerFactory.getLogger(AvatarController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PutMapping()
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
        String pathToFile = "resources/"+ fileName;
        File photo = new File(pathToFile);

        if(photo.createNewFile()) logger.info("The avatar file has been created!");

        byte[] bytes = file.getBytes();
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(pathToFile));
        stream.write(bytes);
        stream.close();

        userEntity.setFileNameAvatar(fileName);

        FileEntity fileEntity = new FileEntity(fileName, userEntity.getId(), new Date(), TypeFile.FILE_AVATAR);

        fileRepository.save(fileEntity);
        userRepository.save(userEntity);
        return new ResponseEntity("User photo changed!", HttpStatus.OK);
    }
}
