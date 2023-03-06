package org.flywithme.adapters;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import lombok.Builder;
import lombok.Data;
import org.flywithme.data.message.ChatMessageDto;
import org.flywithme.data.message.MessageStatus;
import org.flywithme.data.message.UserChatsDto;
import org.flywithme.entity.ChatMessage;
import org.flywithme.entity.ChatRoom;
import org.flywithme.entity.User;
import org.flywithme.ports.spi.ChatPersistencePort;
import org.flywithme.repository.ChatMessageRepository;
import org.flywithme.repository.ChatRoomRepository;
import org.flywithme.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatJpaAdapter implements ChatPersistencePort {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    Mapper mapper = DozerBeanMapperBuilder.buildDefault();

    @Override
    public Optional<String> getChatId(String senderId, String recipientId, boolean createIfNotExist) {
        return chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if(!createIfNotExist){
                        return Optional.empty();
                    }

                    var chatId = String.format("%s_%s", senderId, recipientId);

                    ChatRoom senderRecipient = ChatRoom
                            .builder()
                            .chatId(chatId)
                            .senderId(senderId)
                            .recipientId(recipientId)
                            .build();

                    ChatRoom recipientSender = ChatRoom
                            .builder()
                            .chatId(chatId)
                            .senderId(recipientId)
                            .recipientId(senderId)
                            .build();
                    chatRoomRepository.save(senderRecipient);
                    chatRoomRepository.save(recipientSender);

                    return Optional.of(chatId);
                });
    }

    @Override
    public ChatMessageDto save(ChatMessageDto chatMessageDto) {
        chatMessageDto.setStatus(MessageStatus.RECEIVED);
        var chat = chatMessageRepository.save(mapper.map(chatMessageDto, ChatMessage.class));
        return mapper.map(chat, ChatMessageDto.class);
    }

    @Override
    public Long countNewMessages(String senderId, String recipientId) {
        return chatMessageRepository.countAllBySenderIdAndRecipientIdAndStatus(senderId, recipientId, MessageStatus.RECEIVED);
    }

    @Override
    public List<ChatMessageDto> findChatMessages(String senderId, String recipientId) {
        var chatId = getChatId(senderId, recipientId, false);

        if(chatId.isEmpty()){
            chatId = getChatId(recipientId, senderId, false);
        }

        var messages =
                chatId.map(cId -> chatMessageRepository.findByChatId(cId)).orElse(new ArrayList<>());

        if(messages.size() > 0) {
            updateStatuses(senderId, recipientId, MessageStatus.DELIVERED);
        }

        return messages.stream()
                .map(message -> mapper.map(message, ChatMessageDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ChatMessageDto findById(Long id) {
        return chatMessageRepository.findById(id).map(message -> mapper.map(message, ChatMessageDto.class)).orElse(null);
    }

    @Override
    public List<UserChatsDto> findUserMessages(String id) {
        var chatRooms = chatRoomRepository.findAllBySenderId(id);
        var usersMsgPairs = chatRooms.stream()
                .map(chatRoom -> {
                    var chatId = String.format("%s_%s", chatRoom.getRecipientId(), chatRoom.getSenderId());
                    var user = userRepository.findById(Long.parseLong(chatRoom.getRecipientId()));
                    var lastMessage = chatMessageRepository.findFirstByChatIdOrChatIdOrderByTimestampDesc(chatRoom.getChatId(), chatId);
                    return UserLastMessagePair.builder()
                            .content(lastMessage.getContent())
                            .user(user.get())
                            .timestamp(lastMessage.getTimestamp())
                            .build();
                }).toList();
        return usersMsgPairs.stream().map(this::userToUserChatsDto).toList();
    }

    public void updateStatuses(String senderId, String recipientId, MessageStatus status) {
        List<ChatMessage> chatMessages = chatMessageRepository.findAllBySenderIdAndRecipientId(senderId, recipientId);
        chatMessages.forEach(message -> message.setStatus(status));
        chatMessageRepository.saveAll(chatMessages);
    }

    private UserChatsDto userToUserChatsDto(UserLastMessagePair pairs){
        return UserChatsDto.builder()
                .userId(pairs.user.getId())
                .recipientMainPhoto(pairs.user.getMainPhotoUrl())
                .recipientName(pairs.user.getName())
                .recipientSurname(pairs.user.getSurname())
                .content(pairs.content)
                .timestamp(pairs.timestamp)
                .build();
    }

    @Builder
    @Data
    public static class UserLastMessagePair {
        private User user;
        private String content;
        private Date timestamp;
    }


}
