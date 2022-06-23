package com.community.server.controller;

import com.community.server.entity.UserEntity;
import com.community.server.mapper.SearchUserMapper;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private BlackListRepository blackListRepository;

    @Autowired
    private SearchUserMapper searchUserMapper;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @GetMapping("/id{id}")
    public Object findUserById(HttpServletRequest request, @PathVariable Long id) {

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        UserEntity findUserEntity = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("Find user is not found!"));

        if(blackListRepository.existsByUserIdAndBanId(id, userId))
            return new ResponseEntity("You are blacklisted!", HttpStatus.BAD_REQUEST);

        return searchUserMapper.toModel(findUserEntity);
    }/*

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
