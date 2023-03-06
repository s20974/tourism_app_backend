package org.flywithme.ports.api;

import org.flywithme.data.message.ChatMessageDto;
import org.flywithme.data.message.UserChatsDto;

import java.util.List;
import java.util.Optional;

public interface ChatServicePort {

    Optional<String> getChatId(String senderId, String recipientId, boolean createIfNotExist);

    ChatMessageDto save(ChatMessageDto chatMessageDto);

    Long countNewMessages(String senderId, String recipientId);

    List<ChatMessageDto> findChatMessages(String senderId, String recipientId);

    ChatMessageDto findById(Long id);

    List<UserChatsDto> userMessages(String id);
}
