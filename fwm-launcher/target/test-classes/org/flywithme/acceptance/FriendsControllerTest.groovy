package org.flywithme.acceptance

import org.flywithme.data.FriendDto
import org.flywithme.data.enums.Gender
import org.flywithme.data.enums.Status
import org.flywithme.data.user.UserDto
import org.flywithme.data.user.UserFilterDto
import org.flywithme.entity.User
import org.flywithme.entity.UserFriend
import org.flywithme.repository.UserFriendRepository
import org.flywithme.repository.UserRepository
import org.flywithme.test.AcceptanceBaseSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FriendsControllerTest extends AcceptanceBaseSpec{

    @Autowired
    UserFriendRepository userFriendRepository

    @Autowired
    UserRepository userRepository

    def cleanup(){
        userFriendRepository.deleteAll()
        userRepository.deleteAll()
    }

    def setup() {
        User user = User.builder()
                .name("AlfaUser")
                .surname("Test")
                .password("1111E-!")
                .email("alfa@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()
        userRepository.save(user)

        User user2 = User.builder()
                .name("BetaUser")
                .surname("Test")
                .password("1111E-!")
                .email("beta@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()
        userRepository.save(user2)

        User user3 = User.builder()
                .name("GammaUser")
                .surname("Test")
                .password("1111E-!")
                .email("gamma@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()
        userRepository.save(user3)

        User user4 = User.builder()
                .name("DeltaUser")
                .surname("Test")
                .password("1111E-!")
                .email("delta@gmail.com")
                .emailConfirmed(true)
                .status(Status.NO_WANTING)
                .gender(Gender.MALE)
                .country("Poland")
                .build()
        userRepository.save(user4)

        UserFriend userFriend = new UserFriend();
        userFriend.setRequestTo(user3)
        userFriend.setRequestFrom(user)
        userFriendRepository.save(userFriend)

        UserFriend userFriend2 = new UserFriend();
        userFriend2.setRequestFrom(user)
        userFriend2.setRequestTo(user4)
        userFriend2.setRequestAccepted(true)
        userFriendRepository.save(userFriend2)


    }

    def "Should add friend request to user by id _PosTC"(){
        given:
        def idUser = userRepository.findUserByEmail("alfa@gmail.com").get().id
        def idUser2 = userRepository.findUserByEmail("beta@gmail.com").get().id

        when :
        def response = restTemplate.getForEntity("$BASE_URL:$PORT/api/v1/friends/addFriendRequest?userId=$idUser&friendId=$idUser2",
                                                                            UserDto.class)
        def user = response.getBody()

        then:
        "beta@gmail.com" == user.email
        "BetaUser" == user.name
    }

    def "Should get all friend request by user id _PosTC"(){
        given:
        def idUser2 = userRepository.findUserByEmail("gamma@gmail.com").get().id

        when:
        def response = restTemplate.getForEntity("$BASE_URL:$PORT/api/v1/friends/getAllFriendRequest?id=$idUser2",
                UserDto[].class)
        def listOfUsers = response.getBody()

        then:
        listOfUsers.size() == 1
        ["alfa@gmail.com"] == listOfUsers*.email
        ["AlfaUser"] == listOfUsers*.name
    }

    def "Should accept friend request to user _POsTC"(){
        given:
        def idUser = userRepository.findUserByEmail("alfa@gmail.com").get().id
        def idUser2 = userRepository.findUserByEmail("gamma@gmail.com").get().id

        when:
        restTemplate.getForEntity("$BASE_URL:$PORT/api/v1/friends/addFriend?userId=$idUser2&friendId=$idUser",
                UserDto.class)

        then:
        [true, true] == userFriendRepository.getAllByRequestFromId(idUser)*.requestAccepted

    }

    def "Should get all accepted friends by user id _PosTC"(){
        given:
        def idUser = userRepository.findUserByEmail("alfa@gmail.com").get().id

        when:
        def response = restTemplate.getForEntity("$BASE_URL:$PORT/api/v1/friends/getAllFriends?id=$idUser",
                UserDto[].class)
        def listOfUsers = response.getBody()

        then:
        1 == listOfUsers.size()
        ["delta@gmail.com"] == listOfUsers*.getEmail()
        ["DeltaUser"] == listOfUsers*.getName()
    }

    def "Should delete friend by user id _PosTC"(){
        given:
        def user = userRepository.findUserByEmail("alfa@gmail.com").get()
        def user2 = userRepository.findUserByEmail("delta@gmail.com").get()
        def idUser = user.getId()
        def idUser2 = user2.getId()

        when:
        restTemplate.delete("$BASE_URL:$PORT/api/v1/friends/deleteFriend?userId=$idUser&friendId=$idUser2")

        then:
        userFriendRepository.getFirstByRequestFromAndRequestTo(user, user2).isEmpty()
    }

    def "Should get friend data by friend id _PosTC"(){
        given :
        def idUser = userRepository.findUserByEmail("alfa@gmail.com").get().getId()
        def idUser2 = userRepository.findUserByEmail("delta@gmail.com").get().getId()

        when:
        def response = restTemplate.getForEntity("$BASE_URL:$PORT/api/v1/friends/getFriendData?userId=$idUser&friendId=$idUser2",
                FriendDto.class)
        def friend = response.getBody()

        then:
        "delta@gmail.com" == friend.getEmail()
        "DeltaUser" == friend.getName()
    }

    def "Should get users by filter _PosTC"(){
        given:
        def status = "FOUND"
        def gender = "MALE"
        def country = "Poland"

        when:
        def response = restTemplate.getForEntity("$BASE_URL:$PORT/api/v1/friends/getUsers?gender=$gender&status=$status&country=$country&page=0&size=30&sort=name",
                UserFilterDto[].class)
        def filteredUsers = response.getBody()

        then:
        3 == filteredUsers.size()
        ["FOUND", "FOUND", "FOUND"] == filteredUsers*.getStatus()*.toString()
        ["MALE", "MALE", "MALE"] == filteredUsers*.getGender()*.toString()
        ["Poland", "Poland", "Poland"] == filteredUsers*.getCountry()
    }
}
