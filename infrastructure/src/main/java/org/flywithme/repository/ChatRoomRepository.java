package org.flywithme.repository;

import org.flywithme.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);

    List<ChatRoom> findAllBySenderId(String senderId);
}
