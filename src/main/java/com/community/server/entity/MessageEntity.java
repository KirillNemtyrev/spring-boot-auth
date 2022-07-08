package com.community.server.entity;

import com.community.server.enums.MessageType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="messages")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long chatId;

    @NotNull
    private Long senderId;

    private String text;

    @CreatedDate
    private Date sendDate = new Date();

    private MessageType messageType = MessageType.ALL_VISION;

    public MessageEntity() {}
    public MessageEntity(Long chatId, Long senderId, String text){
        this.chatId = chatId;
        this.senderId = senderId;
        this.text = text;
    }
}
