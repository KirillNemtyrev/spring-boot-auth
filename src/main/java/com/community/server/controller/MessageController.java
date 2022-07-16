package com.community.server.controller;

import com.community.server.body.MessageBody;
import com.community.server.dto.MessageDto;
import com.community.server.entity.ChatRoomEntity;
import com.community.server.entity.MessageEntity;
import com.community.server.entity.UserEntity;
import com.community.server.enums.MessageType;
import com.community.server.events.DeleteChatRoom;
import com.community.server.events.MessageNew;
import com.community.server.events.MessageSend;
import com.community.server.exception.BadRequestException;
import com.community.server.repository.BlackListRepository;
import com.community.server.repository.ChatRoomRepository;
import com.community.server.repository.MessageRepository;
import com.community.server.repository.UserRepository;
import com.community.server.security.JwtAuthenticationFilter;
import com.community.server.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/chatId{chatId}")
    public Object getChatMessage(HttpServletRequest request, @PathVariable Long chatId){

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        if(!userRepository.existsById(userId))
            return new UsernameNotFoundException("User is not found!");

        ChatRoomEntity chatRoomEntity = chatRoomRepository.findById(chatId).orElseThrow(
                () -> new BadRequestException("Chat not found!"));

        if(!chatRoomEntity.getRecipientId().equals(userId) && !chatRoomEntity.getSenderId().equals(userId))
            return new BadRequestException("You no have access for this chat!");

        List<MessageEntity> messageEntities = messageRepository.findByChatId(chatId);
        List<MessageDto> messageDtoList = new ArrayList<>();

        for(MessageEntity messageEntity : messageEntities) {
            if(messageEntity.getMessageType() == MessageType.NO_VISION ||
                    (messageEntity.getMessageType() == MessageType.RECIPIENT_VISION && chatRoomEntity.getSenderId().equals(userId)) ||
                    (messageEntity.getMessageType() == MessageType.SENDER_VISION && chatRoomEntity.getRecipientId().equals(userId)))
                continue;

            messageDtoList.add(
                    new MessageDto(messageEntity.getId(), messageEntity.getChatId(), messageEntity.getSenderId(), messageEntity.getText(), messageEntity.getSendDate().getTime()));
        }

        MessageEntity messageEntity = messageRepository.findFirstByChatIdOrderByIdDesc(chatId).orElse(null);
        if(messageEntity != null && !messageEntity.getSenderId().equals(userId)) {
            chatRoomEntity.setCountNewMessage(0L);
            chatRoomRepository.save(chatRoomEntity);
        }
        return messageDtoList;
    }

    @PostMapping
    public Object sendMessageToChat(HttpServletRequest request, @Valid @RequestBody MessageBody messageBody){
        if(messageBody.getChatId() == null)
            return new BadRequestException("Chat Id is null!");

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        ChatRoomEntity chatRoomEntity = chatRoomRepository.findById(messageBody.getChatId()).orElseThrow(
                () -> new BadRequestException("Chat not found!"));

        if(!chatRoomEntity.getRecipientId().equals(userId) && !chatRoomEntity.getSenderId().equals(userId))
            return new BadRequestException("You no have access this chat!");

        UserEntity userEntity = userRepository.findById(chatRoomEntity.getSenderId().equals(userId) ? chatRoomEntity.getSenderId() : chatRoomEntity.getRecipientId()).orElseThrow(
                () -> new UsernameNotFoundException("Sender user is not found!"));

        UserEntity recipientEntity = userRepository.findById(chatRoomEntity.getSenderId().equals(userId) ? chatRoomEntity.getRecipientId() : chatRoomEntity.getSenderId()).orElseThrow(
                () -> new UsernameNotFoundException("Recipient user is not found!"));

        MessageEntity messageEntity = new MessageEntity(messageBody.getChatId(), userId, messageBody.getText());
        chatRoomEntity.setCountNewMessage(chatRoomEntity.getCountNewMessage() + 1);

        messageRepository.save(messageEntity);
        chatRoomRepository.save(chatRoomEntity);

        messagingTemplate.convertAndSendToUser(userEntity.getUuid(), "/events", new MessageSend(messageEntity.getChatId(), recipientEntity.getId(), messageEntity.getText(), messageEntity.getSendDate()));
        messagingTemplate.convertAndSendToUser(recipientEntity.getUuid(), "/events", new MessageNew(messageEntity.getChatId(), userEntity.getId(), messageEntity.getText(), messageEntity.getSendDate(), chatRoomEntity.getCountNewMessage()));
        return new ResponseEntity("Your message sent!" , HttpStatus.OK);
    }
}
