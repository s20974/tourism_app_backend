package org.flywithme.controller;

import lombok.RequiredArgsConstructor;
import org.flywithme.data.message.ChatMessageDto;
import org.flywithme.data.message.ChatNotification;
import org.flywithme.data.message.UserChatsDto;
import org.flywithme.ports.api.ChatServicePort;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000",  allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatServicePort chatServicePort;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageDto chatMessageDto) {
        var chatId = chatServicePort
                .getChatId(chatMessageDto.getSenderId(), chatMessageDto.getRecipientId(), true);
        chatMessageDto.setChatId(chatId.get());

        ChatMessageDto saved = chatServicePort.save(chatMessageDto);

        messagingTemplate.convertAndSendToUser(
                chatMessageDto.getRecipientId(),"/queue/messages",
                new ChatNotification(
                        saved.getId(),
                        saved.getSenderId()));
    }

    @GetMapping("/messages/{senderId}/{recipientId}/count")
    public Long countNewMessages(@PathVariable String senderId, @PathVariable String recipientId) {
        return chatServicePort.countNewMessages(senderId, recipientId);
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public List<ChatMessageDto> findChatMessages (@PathVariable String senderId, @PathVariable String recipientId) {
        return chatServicePort.findChatMessages(senderId, recipientId);
    }

    @GetMapping("/messages/{id}")
    public ChatMessageDto findMessage (@PathVariable Long id) {
        return chatServicePort.findById(id);
    }

    @GetMapping("/user-chats")
    public List<UserChatsDto> findMessages(@RequestParam(name = "id") String userId) {
        return chatServicePort.userMessages(userId);
    }
}
