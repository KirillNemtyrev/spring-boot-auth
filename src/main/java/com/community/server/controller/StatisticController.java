package com.community.server.controller;

import com.community.server.dto.ChatRoom;
import com.community.server.dto.ProfileStatistic;
import com.community.server.entity.ChatRoomEntity;
import com.community.server.entity.UserEntity;
import com.community.server.enums.ChatRoomVisible;
import com.community.server.enums.CommentVisible;
import com.community.server.enums.ProfileStatisticVisible;
import com.community.server.repository.BlackListRepository;
import com.community.server.repository.ChatRoomRepository;
import com.community.server.repository.UserRepository;
import com.community.server.security.JwtAuthenticationFilter;
import com.community.server.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api/statistic")
@RestController
public class StatisticController {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlackListRepository blackListRepository;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/chats/id{id}")
    public Object getChatsElement(HttpServletRequest request, @PathVariable Long id) {

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        if(!userRepository.existsById(userId))
            return new UsernameNotFoundException("User is not found!");

        UserEntity userEntity = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("This user is not found!"));

        if(((userEntity.getVisibleMyChats() == ProfileStatisticVisible.MY_CHATS_VISION && !chatRoomRepository.existsBySenderIdOrRecipientId(userId, id)) ||
                (userEntity.getVisibleMyChats() == ProfileStatisticVisible.NO_VISION)) && !userId.equals(id))
            return new ResponseEntity("This element statistic close!", HttpStatus.BAD_REQUEST);

        // Variables
        List<ChatRoomEntity> chatRoomEntities = chatRoomRepository.findBySenderIdOrRecipientId(id, id);
        List<ProfileStatistic> profileStatisticList = new ArrayList<>(chatRoomEntities.size());

        for (ChatRoomEntity chatRoomEntity : chatRoomEntities) {

            UserEntity user =
                    userRepository.findById(userEntity.getId().equals(chatRoomEntity.getSenderId()) ? chatRoomEntity.getRecipientId() : chatRoomEntity.getSenderId()).orElseThrow(
                            () -> new UsernameNotFoundException("Not found user!"));

            if((user.getId().equals(chatRoomEntity.getSenderId()) && chatRoomEntity.getChatRoomVisible() == ChatRoomVisible.RECIPIENT_VISION) ||
                    (user.getId().equals(chatRoomEntity.getRecipientId()) && chatRoomEntity.getChatRoomVisible() == ChatRoomVisible.SENDER_VISION) ||
                    chatRoomEntity.getChatRoomVisible() == ChatRoomVisible.NO_VISION) continue;

            ProfileStatistic profileStatistic = new ProfileStatistic(
                    id.equals(chatRoomEntity.getSenderId()) ? chatRoomEntity.getRecipientId() : chatRoomEntity.getSenderId(),
                    user.getUsername(),
                    user.getName(),
                    user.getFileNameAvatar());

            profileStatisticList.add(profileStatistic);
        }
        return profileStatisticList;
    }

}
