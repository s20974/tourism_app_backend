package org.flywithme.data.message;

import lombok.Data;

import java.util.Date;

@Data
public class ChatMessageDto {
    private Long id;
    private String chatId;
    private String senderId;
    private String recipientId;
    private String content;
    private Date timestamp;
    private MessageStatus status;
}
