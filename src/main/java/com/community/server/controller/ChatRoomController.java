package com.community.server.controller;

import com.community.server.dto.ChatRoom;
import com.community.server.entity.ChatRoomEntity;
import com.community.server.entity.MessageEntity;
import com.community.server.entity.UserEntity;
import com.community.server.enums.ChatRoomVisible;
import com.community.server.events.DeleteChatRoom;
import com.community.server.exception.AppException;
import com.community.server.repository.BlackListRepository;
import com.community.server.repository.ChatRoomRepository;
import com.community.server.repository.MessageRepository;
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
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/chatroom")
public class ChatRoomController {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping()
    public Object getChats(HttpServletRequest request) {

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        if(!userRepository.existsById(userId))
            return new UsernameNotFoundException("User is not found!");

        // Variables
        List<ChatRoomEntity> chatRoomEntities = chatRoomRepository.findBySenderIdOrRecipientId(userId, userId);
        List<ChatRoom> chatRoomList = new ArrayList<>(chatRoomEntities.size());

        for (ChatRoomEntity chatRoomEntity : chatRoomEntities) {

            UserEntity userEntity =
                    userRepository.findById(userId.equals(chatRoomEntity.getSenderId()) ? chatRoomEntity.getRecipientId() : chatRoomEntity.getSenderId()).orElseThrow(
                            () -> new UsernameNotFoundException("Not found user!"));

            MessageEntity messageEntity = messageRepository.findFirstByChatIdOrderByIdDesc(chatRoomEntity.getId()).orElse(null);

            if((userId.equals(chatRoomEntity.getSenderId()) && chatRoomEntity.getChatRoomVisible() == ChatRoomVisible.RECIPIENT_VISION) ||
                    (userId.equals(chatRoomEntity.getRecipientId()) && chatRoomEntity.getChatRoomVisible() == ChatRoomVisible.SENDER_VISION) ||
                        chatRoomEntity.getChatRoomVisible() == ChatRoomVisible.NO_VISION) continue;

            ChatRoom chatRoom = new ChatRoom(
                    chatRoomEntity.getId(),
                    (userId.equals(chatRoomEntity.getSenderId())) ? chatRoomEntity.getRecipientId() : chatRoomEntity.getSenderId(),
                    userEntity.getUsername(),
                    userEntity.getName(),
                    userEntity.getFileNameAvatar(),
                    messageEntity != null ? messageEntity.getSenderId() : null,
                    messageEntity != null ? messageEntity.getText() : "",
                    messageEntity != null ? messageEntity.getSendDate().getTime() : chatRoomEntity.getCreatedDate().getTime(),
                    messageEntity != null ? messageEntity.getSenderId().equals(userId) ? 0 : chatRoomEntity.getCountNewMessage() : null);

            chatRoomList.add(chatRoom);
        }
        return chatRoomList;
    }

    @DeleteMapping("/id{id}/{everyone}")
    public Object deleteRoom(HttpServletRequest request, @PathVariable Long id, @PathVariable Boolean everyone) {

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        ChatRoomEntity chatRoom = chatRoomRepository.findById(id).orElseThrow(
                () -> new AppException("Chat room not found!"));

        if(!Objects.equals(chatRoom.getSenderId(), userId) && !Objects.equals(chatRoom.getRecipientId(), userId))
            return new ResponseEntity("Chat room not found!", HttpStatus.BAD_REQUEST);

        chatRoom.setChatRoomVisible(
                everyone ? ChatRoomVisible.NO_VISION :
                        userId.equals(chatRoom.getSenderId()) ? ChatRoomVisible.RECIPIENT_VISION : ChatRoomVisible.SENDER_VISION);

        messagingTemplate.convertAndSendToUser(userEntity.getUuid(),"/events", new DeleteChatRoom(chatRoom.getId()));

        chatRoomRepository.save(chatRoom);
        return new ResponseEntity("The chat room has been deleted!", HttpStatus.OK);
    }

    @DeleteMapping("/counter/id{id}")
    public Object resetCounter(HttpServletRequest request, @PathVariable Long id){

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        if(!userRepository.existsById(userId))
            return new UsernameNotFoundException("User is not found!");

        ChatRoomEntity chatRoom = chatRoomRepository.findById(id).orElseThrow(
                () -> new AppException("Chat room not found!"));

        if(!Objects.equals(chatRoom.getSenderId(), userId) && !Objects.equals(chatRoom.getRecipientId(), userId))
            return new ResponseEntity("Chat room not found!", HttpStatus.BAD_REQUEST);

        MessageEntity messageEntity = messageRepository.findFirstByChatIdOrderByIdDesc(chatRoom.getId()).orElse(null);

        if(messageEntity == null || messageEntity.getSenderId().equals(userId))
            return new ResponseEntity("The message counter is already empty!", HttpStatus.BAD_REQUEST);

        chatRoom.setCountNewMessage(0L);
        chatRoomRepository.save(chatRoom);
        return new ResponseEntity("The message counter has been reset!", HttpStatus.OK);
    }
}
