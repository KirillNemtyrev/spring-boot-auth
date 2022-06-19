package com.community.server.controller;

import com.community.server.entity.UserEntity;
import com.community.server.mapper.SettingsUserMapper;
import com.community.server.repository.BlackListRepository;
import com.community.server.repository.FileRepository;
import com.community.server.repository.UserRepository;
import com.community.server.security.JwtAuthenticationFilter;
import com.community.server.security.JwtTokenProvider;
import com.community.server.service.MailService;
import com.community.server.service.UserService;
import com.community.server.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.UnknownHostException;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private BlackListRepository blackListRepository;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SettingsUserMapper settingsUserMapper;

    @Autowired
    public UserService userService;

    @Autowired
    public MailService mailService;

    @Value("${app.resetExpirationInMs}")
    private int resetExpirationInMs;


    /*@PostMapping("/change/avatar")
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

    @GetMapping("/id{id}")
    public UserFindView findUserById(HttpServletRequest request, @PathVariable Long id) {

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        UserEntity findUserEntity = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("Find user is not found!"));

        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("dd MMMM yyyy", new Locale("ru", "RU"));

        UserFindView userFindView = new UserFindView(
                findUserEntity.getId(),
                findUserEntity.getName(),
                findUserEntity.getUsername(),
                findUserEntity.getFileNameAvatar(),
                (findUserEntity.getShowContactEmail() ? findUserEntity.getContactEmail() : "Email hidden"),
                (findUserEntity.getShowContactPhone() ? findUserEntity.getContactPhone() : "Phone hidden"),
                simpleDateFormat.format(findUserEntity.getCreateDate()),
                findUserEntity.getType());

        if(blackListRepository.existsByUserIdAndBanId(findUserEntity.getId(), userEntity.getId())) {
            userFindView.setContactEmail("Can not see");
            userFindView.setContactPhone("Can not see");

            userFindView.setLimitedToHisPage(Boolean.TRUE);
            userFindView.setCreatedDate(null);
        }

        if(blackListRepository.existsByUserIdAndBanId(userEntity.getId(), findUserEntity.getId()))
            userFindView.setLimitedOnMyPage(Boolean.TRUE);

        return userFindView;
    }

    @GetMapping("/{username}")
    public UserFindView findUserById(HttpServletRequest request, @PathVariable String username) {

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        UserEntity findUserEntity = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Find user is not found!"));

        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("dd MMMM yyyy", new Locale("ru", "RU"));

        UserFindView userFindView = new UserFindView(
                findUserEntity.getId(),
                findUserEntity.getName(),
                findUserEntity.getUsername(),
                findUserEntity.getFileNameAvatar(),
                (findUserEntity.getShowContactEmail() ? findUserEntity.getContactEmail() : "Email hidden"),
                (findUserEntity.getShowContactPhone() ? findUserEntity.getContactPhone() : "Phone hidden"),
                simpleDateFormat.format(findUserEntity.getCreateDate()),
                findUserEntity.getType());

        if(blackListRepository.existsByUserIdAndBanId(findUserEntity.getId(), userEntity.getId())) {
            userFindView.setContactEmail("Can not see");
            userFindView.setContactPhone("Can not see");

            userFindView.setLimitedToHisPage(Boolean.TRUE);
            userFindView.setCreatedDate(null);
        }

        if(blackListRepository.existsByUserIdAndBanId(userEntity.getId(), findUserEntity.getId()))
            userFindView.setLimitedOnMyPage(Boolean.TRUE);

        return userFindView;
    }

    @GetMapping("/find/{name}")
    public List<FindView> findUser(@PathVariable String name) throws UnknownHostException {

        List<UserEntity> users = userRepository.findByUsernameContainingIgnoreCase(name);
        List<FindView> findViewList = new ArrayList<>();

        for(int i = 0; i < users.size(); i++) {
            FindView findView = new FindView(
                    users.get(i).getId(),
                    users.get(i).getName(),
                    users.get(i).getUsername(),
                    users.get(i).getFileNameAvatar()
            );
            findViewList.add(findView);
        }

        return findViewList;
    }*/
}
