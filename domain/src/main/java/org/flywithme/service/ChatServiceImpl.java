package org.flywithme.service;

import lombok.RequiredArgsConstructor;
import org.flywithme.data.message.ChatMessageDto;
import org.flywithme.data.message.UserChatsDto;
import org.flywithme.ports.api.ChatServicePort;
import org.flywithme.ports.spi.ChatPersistencePort;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ChatServiceImpl implements ChatServicePort {

    private final ChatPersistencePort chatPersistencePort;

    @Override
    public Optional<String> getChatId(String senderId, String recipientId, boolean createIfNotExist) {
        return chatPersistencePort.getChatId(senderId, recipientId, createIfNotExist);
    }

    @Override
    public ChatMessageDto save(ChatMessageDto chatMessageDto) {
        return chatPersistencePort.save(chatMessageDto);
    }

    @Override
    public Long countNewMessages(String senderId, String recipientId) {
        return chatPersistencePort.countNewMessages(senderId, recipientId);
    }

    @Override
    public List<ChatMessageDto> findChatMessages(String senderId, String recipientId) {
        return chatPersistencePort.findChatMessages(senderId, recipientId);
    }

    @Override
    public ChatMessageDto findById(Long id) {
        return chatPersistencePort.findById(id);
    }

    @Override
    public List<UserChatsDto> userMessages(String id) {
        return chatPersistencePort.findUserMessages(id);
    }
}
