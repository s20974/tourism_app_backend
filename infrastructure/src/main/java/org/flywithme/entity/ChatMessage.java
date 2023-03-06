package org.flywithme.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flywithme.data.message.MessageStatus;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "chat_message_tb")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatId;

    private String senderId;

    private String recipientId;

    private String content;

    private Date timestamp;

    @Enumerated(EnumType.STRING)
    private MessageStatus status;
}
