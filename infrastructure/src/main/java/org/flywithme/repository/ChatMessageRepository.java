package org.flywithme.repository;

import org.flywithme.data.message.MessageStatus;
import org.flywithme.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatId(String chatId);

    Long countAllBySenderIdAndRecipientIdAndStatus(String senderId, String recipientId, MessageStatus status);

    List<ChatMessage> findAllBySenderIdAndRecipientId(String senderId, String recipientId);

    ChatMessage findFirstByChatIdOrChatIdOrderByTimestampDesc(String chatId, String chatId2);
}
