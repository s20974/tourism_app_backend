package org.flywithme.acceptance

import org.flywithme.data.FriendDto
import org.flywithme.data.message.ChatMessageDto
import org.flywithme.data.message.UserChatsDto
import org.flywithme.entity.ChatRoom
import org.flywithme.entity.User
import org.flywithme.ports.api.ChatServicePort
import org.flywithme.repository.ChatMessageRepository
import org.flywithme.repository.ChatRoomRepository
import org.flywithme.repository.UserRepository
import org.flywithme.test.AcceptanceBaseSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatControllerTest extends AcceptanceBaseSpec{

    @Autowired
    ChatRoomRepository chatRoomRepository

    @Autowired
    ChatMessageRepository chatMessageRepository

    @Autowired
    ChatServicePort chatServicePort

    @Autowired
    UserRepository userRepository

    def cleanup(){
        chatMessageRepository.deleteAll()
        chatRoomRepository.deleteAll()
        userRepository.deleteAll()
    }

    def setup(){
        User user = User.builder()
                .name("AlfaUser")
                .surname("Test")
                .password("1111E-!")
                .email("alfa@gmail.com")
                .emailConfirmed(true)
                .build()
        userRepository.save(user)

        User user2 = User.builder()
                .name("BetaUser")
                .surname("Test")
                .password("1111E-!")
                .email("beta@gmail.com")
                .emailConfirmed(true)
                .build()
        userRepository.save(user2)

        def senderId = userRepository.findUserByEmail("alfa@gmail.com").get().getId()
        def recipientId = userRepository.findUserByEmail("beta@gmail.com").get().getId()

        def chatId = String.format("%s_%s", senderId, recipientId);

        ChatRoom senderRecipient = ChatRoom
                .builder()
                .chatId(chatId)
                .senderId(senderId.toString())
                .recipientId(recipientId.toString())
                .build();

        ChatRoom recipientSender = ChatRoom
                .builder()
                .chatId(chatId)
                .senderId(recipientId.toString())
                .recipientId(senderId.toString())
                .build();
        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);

        ChatMessageDto chatMessageDto = new ChatMessageDto()
        chatMessageDto.setChatId(chatId)
        chatMessageDto.setRecipientId(recipientId.toString())
        chatMessageDto.setSenderId(senderId.toString())
        chatMessageDto.setContent("test message")
        chatServicePort.save(chatMessageDto)
    }

    def "Should count new messages between users _PosTC"(){
        given:
        def userId = userRepository.findUserByEmail("alfa@gmail.com").get().getId()
        def userId2 = userRepository.findUserByEmail("beta@gmail.com").get().getId()

        when:
        def response = restTemplate.getForEntity("$BASE_URL:$PORT/messages/$userId/$userId2/count",
                Long.class)
        def count = response.getBody()

        then:
        1 == count
    }

    def "Should get chat messages beteen users _PosTC"(){
        given:
        def senderId = userRepository.findUserByEmail("alfa@gmail.com").get().getId()
        def recipientId = userRepository.findUserByEmail("beta@gmail.com").get().getId()

        when:
        def response = restTemplate.getForEntity("$BASE_URL:$PORT/messages/$senderId/$recipientId",
                ChatMessageDto[].class)
        def messages = response.getBody()

        then:
        ["test message"] == messages*.content
    }

    def "Should return message by id _PosTC"(){
        given:
        def senderId = userRepository.findUserByEmail("alfa@gmail.com").get().getId()
        def recipientId = userRepository.findUserByEmail("beta@gmail.com").get().getId()
        def chatId = String.format("%s_%s", senderId, recipientId);
        def messageId = chatMessageRepository.findByChatId(chatId).first().id

        when:
        def response = restTemplate.getForEntity("$BASE_URL:$PORT/messages/$messageId",
                ChatMessageDto.class)
        def message = response.getBody()

        then:
        "test message" == message.content
    }
}
