package com.community.server.controller;

import com.community.server.body.SearchUserBody;
import com.community.server.entity.UserEntity;
import com.community.server.enums.ProfileStatisticVisible;
import com.community.server.enums.UserStatus;
import com.community.server.mapper.SearchUserMapper;
import com.community.server.repository.*;
import com.community.server.security.JwtAuthenticationFilter;
import com.community.server.security.JwtTokenProvider;
import com.community.server.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlackListRepository blackListRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private InviteRepository inviteRepository;

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

        if(!userRepository.existsById(userId))
            return new UsernameNotFoundException("User is not found!");

        UserEntity userEntity = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("Find user is not found!"));

        if(blackListRepository.existsByUserIdAndBanId(id, userId))
            return new ResponseEntity("You are blacklisted!", HttpStatus.BAD_REQUEST);

        UserSearch userSearch = new UserSearch(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getUsername(),
                userEntity.getAboutMe(),
                userEntity.getFileNameAvatar()
        );
        userSearch.setCountChats(chatRoomRepository.countBySenderIdOrRecipientId(id, id));
        userSearch.setCountInvite(inviteRepository.countByUserId(id));

        if(((userEntity.getVisibleMyChats() == ProfileStatisticVisible.MY_CHATS_VISION && !chatRoomRepository.existsBySenderIdOrRecipientId(userId, id))
            || (userEntity.getVisibleMyChats() == ProfileStatisticVisible.NO_VISION)) && userId != id)
            userSearch.setCountChats(null);

        if(((userEntity.getVisibleMyInvite() == ProfileStatisticVisible.MY_CHATS_VISION && !chatRoomRepository.existsBySenderIdOrRecipientId(userId, id))
                || (userEntity.getVisibleMyInvite() == ProfileStatisticVisible.NO_VISION)) && userId != id)
            userSearch.setCountInvite(null);

        if(userId != id)
            userSearch.setUserStatus(
                    chatRoomRepository.existsBySenderIdOrRecipientId(userId, id) ? UserStatus.HAVE_CHAT :
                            inviteRepository.existsByUserIdAndInviteId(id, userId) ? UserStatus.YOU_WAIT_ACCEPT :
                                    inviteRepository.existsByUserIdAndInviteId(userId, id) ? UserStatus.HE_WAIT_ACCEPT : UserStatus.NO_FRIEND);

        if(blackListRepository.existsByUserIdAndBanId(userId, id))
            userSearch.setInBlackList(Boolean.TRUE);

        return userSearch;
    }

    @PostMapping("/search")
    public Object search(HttpServletRequest request, @Valid @RequestBody SearchUserBody searchUserBody) {

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        if(!userRepository.existsById(userId))
            return new UsernameNotFoundException("User is not found!");

        String name = searchUserBody.getNameOrUsername();
        List<UserEntity> users = userRepository.findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(name, name);
        List<UserSearch> userSearchList = new ArrayList<>();

        for(UserEntity userEntity: users){
            if(userEntity.getId() == userId || blackListRepository.existsByUserIdAndBanId(userEntity.getId(), userId))
                continue;

            UserSearch userSearch = new UserSearch(
                    userEntity.getId(),
                    userEntity.getName(),
                    userEntity.getUsername(),
                    userEntity.getAboutMe(),
                    userEntity.getFileNameAvatar()
            );
            userSearch.setCountChats(chatRoomRepository.countBySenderIdOrRecipientId(userEntity.getId(), userEntity.getId()));
            userSearch.setCountInvite(inviteRepository.countByUserId(userEntity.getId()));

            if(((userEntity.getVisibleMyChats() == ProfileStatisticVisible.MY_CHATS_VISION && !chatRoomRepository.existsBySenderIdOrRecipientId(userId, userEntity.getId()))
                    || (userEntity.getVisibleMyChats() == ProfileStatisticVisible.NO_VISION)))
                userSearch.setCountChats(null);

            if(((userEntity.getVisibleMyInvite() == ProfileStatisticVisible.MY_CHATS_VISION && !chatRoomRepository.existsBySenderIdOrRecipientId(userId, userEntity.getId()))
                    || (userEntity.getVisibleMyInvite() == ProfileStatisticVisible.NO_VISION)))
                userSearch.setCountInvite(null);

            userSearch.setUserStatus(
                    chatRoomRepository.existsBySenderIdOrRecipientId(userId, userEntity.getId()) ? UserStatus.HAVE_CHAT :
                            inviteRepository.existsByUserIdAndInviteId(userEntity.getId(), userId) ? UserStatus.YOU_WAIT_ACCEPT :
                                    inviteRepository.existsByUserIdAndInviteId(userId, userEntity.getId()) ? UserStatus.HE_WAIT_ACCEPT : UserStatus.NO_FRIEND);

            if(blackListRepository.existsByUserIdAndBanId(userId, userEntity.getId()))
                userSearch.setInBlackList(Boolean.TRUE);

            userSearchList.add(userSearch);
        }
        return userSearchList;
    }
}
