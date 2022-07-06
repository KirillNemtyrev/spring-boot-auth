package com.community.server.controller;

import com.community.server.dto.BlackList;
import com.community.server.entity.BlackListEntity;
import com.community.server.entity.UserEntity;
import com.community.server.mapper.BlackListMapper;
import com.community.server.repository.BlackListRepository;
import com.community.server.repository.UserRepository;
import com.community.server.security.JwtAuthenticationFilter;
import com.community.server.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/blacklist")
public class BlackListController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlackListRepository blackListRepository;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private BlackListMapper blackListMapper;


    @GetMapping()
    public List<BlackList> blackList(HttpServletRequest request) {

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User is not found!"));

        List<BlackListEntity> blackListRepositoryList = blackListRepository.findByUserId(userEntity.getId());
        List<BlackList> blackList = new ArrayList<>(blackListRepositoryList.size());

        for(BlackListEntity blackListEntity : blackListRepositoryList) {
            UserEntity userLimitedEntity = userRepository.findById(blackListEntity.getUserId()).orElseThrow(
                    () -> new UsernameNotFoundException("User is not found!"));

            blackList.add(blackListMapper.toModel(userLimitedEntity));
        }

        return blackList;
    }

    @GetMapping("/id{id}")
    public Object getUserInBlacklist(HttpServletRequest request, @PathVariable Long id){

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        if(!userRepository.existsById(id))
            return new UsernameNotFoundException("User is not found!");

        if(!userRepository.existsById(id))
            return new UsernameNotFoundException("Find user is not found!");

        if(blackListRepository.existsByUserIdAndBanId(userId, id))
            return new ResponseEntity("The user is already blacklisted!", HttpStatus.NOT_FOUND);

        return new ResponseEntity("The user is not blacklisted!", HttpStatus.OK);
    }

    @PostMapping("/id{id}")
    public ResponseEntity<?> addBlackList(HttpServletRequest request, @PathVariable Long id) {

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        UserEntity findUserEntity = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("Find user is not found!"));

        if(blackListRepository.existsByUserIdAndBanId(userEntity.getId(), findUserEntity.getId()))
            return new ResponseEntity("The user is already blacklisted!", HttpStatus.BAD_REQUEST);

        BlackListEntity blackListEntity = new BlackListEntity(userEntity.getId(), findUserEntity.getId());

        blackListRepository.save(blackListEntity);
        return new ResponseEntity("The user has been blacklisted!", HttpStatus.OK);
    }

    @DeleteMapping("/id{id}")
    public ResponseEntity<?> deleteBlackList(HttpServletRequest request, @PathVariable Long id) {

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        UserEntity findUserEntity = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("Find user is not found!"));

        BlackListEntity blackListEntity = blackListRepository.findByUserIdAndBanId(userEntity.getId(), findUserEntity.getId()).orElseThrow(
                () -> new UsernameNotFoundException("The user is not blacklisted!"));

        blackListRepository.delete(blackListEntity);
        return new ResponseEntity("The user has been removed from the list!", HttpStatus.OK);
    }
}
