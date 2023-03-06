package org.flywithme.unit.adapters

import org.flywithme.adapters.UserJpaAdapter
import org.flywithme.adapters.aws.AmazonS3ImageService
import org.flywithme.data.enums.Gender
import org.flywithme.data.enums.Status
import org.flywithme.data.user.UserDto
import org.flywithme.data.user.UserRegisterDto
import org.flywithme.entity.PhotoComplaint
import org.flywithme.entity.Post
import org.flywithme.entity.User
import org.flywithme.entity.UserFriend
import org.flywithme.entity.UserGallery
import org.flywithme.exceptions.IncorrectLoginOrPassword
import org.flywithme.exceptions.UserNotFoundException
import org.flywithme.repository.PhotoComplaintRepository
import org.flywithme.repository.UserFriendRepository
import org.flywithme.repository.UserGalleryRepository
import org.flywithme.repository.UserRepository
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class UserJpaAdapterTest extends Specification{

    UserRepository userRepository = Mock()
    UserFriendRepository userFriendRepository = Mock()
    PasswordEncoder passwordEncoder = Mock()
    JavaMailSender mailSender = Mock()
    AmazonS3ImageService amazonS3ImageService = Mock()
    UserGalleryRepository userGalleryRepository = Mock()
    PhotoComplaintRepository photoComplaintRepository = Mock()
    String fromAddress = 'devflywithme@gmail.com'
    String senderName = 'FlyWithMe'
    String verifyUrl = 'localhost:3000/verify/'

    UserJpaAdapter userJpaAdapter = new UserJpaAdapter(userRepository, userFriendRepository, passwordEncoder,
        mailSender, amazonS3ImageService, fromAddress, senderName, verifyUrl, userGalleryRepository, photoComplaintRepository)

    def "Should verify user email Pos_TC"() {
        given:
        def user = User.builder()
            .name("Name")
            .surname("Surname")
            .verificationCode("1111")
            .emailConfirmed(false)
            .build()

        userRepository.findUserByVerificationCode('1111') >> Optional.of(user)

        when:
        def result = userJpaAdapter.verifyEmail("1111")

        then:
        1 * userRepository.save(_ as User)
        result
    }

    def "Should not verify user email Neg_TC"() {
        given:
        def user = User.builder()
                .name("Name")
                .surname("Surname")
                .verificationCode("1111")
                .emailConfirmed(false)
                .build()

        1 * userRepository.findUserByVerificationCode('1111') >> Optional.empty()

        when:
        def result = userJpaAdapter.verifyEmail("1111")

        then:
        0 * userRepository.save(_ as User)
        !result
    }

    def "Should return user information by email Pos_TC"(){
        given:
        def user = User.builder()
                .name("Name")
                .surname("Surname")
                .email("test01@gmail.com")
                .emailConfirmed(false)
                .userFriends(new ArrayList<UserFriend>())
                .posts(new ArrayList<Post>())
                .build()
        1 * userRepository.findUserByEmail("test01@gmail.com") >> Optional.of(user)

        when:
        def result = userJpaAdapter.getUserInformationByEmail("test01@gmail.com")

        then:
        result instanceof UserDto
        user.getName() == result.getName()
        user.getEmail() == result.getEmail()
    }

    def "Should not return user information by email Neg_TC"(){
        given:
        def user = User.builder()
                .name("Name")
                .surname("Surname")
                .email("test01@gmail.com")
                .emailConfirmed(false)
                .build()
        1 * userRepository.findUserByEmail("test01@gmail.com") >>  Optional.empty()

        when:
        def result = userJpaAdapter.getUserInformationByEmail("test01@gmail.com")

        then:
        result == null
    }

    def "Should change user data Pos_TC"() {
        given:
        def oldUser = User.builder()
                .email("oldEmail@gmail.com")
                .password("A90210edfyz-")
                .name("Name")
                .surname("Surname")
                .build()
        def newuser = UserDto.builder()
                .email("oldEmail@gmail.com")
                .password("A90210edfyz-New")
                .name("NewName")
                .surname("NewSurname")
                .build()
        def savedUser = User.builder()
                .email("oldEmail@gmail.com")
                .password("A90210edfyz-New")
                .name("NewName")
                .surname("NewSurname")
                .build()

        1 * userRepository.findUserByEmail("oldEmail@gmail.com") >> Optional.of(oldUser)
        1 * userRepository.save(_ as User) >> savedUser

        when:
        def result = userJpaAdapter.changeUserData("oldEmail@gmail.com", newuser)

        then:
        newuser.getEmail() == result.getEmail()
    }

    def "Should not change user data Neg_TC"() {
        given:
        def oldUser = User.builder()
                .email("oldEmail@gmail.com")
                .password("A90210edfyz-")
                .name("Name")
                .surname("Surname")
                .build()
        def newuser = UserDto.builder()
                .email("oldEmail@gmail.com")
                .password("A90210edfyz-New")
                .oldPassword("A90210edfyz-Fail")
                .name("NewName")
                .surname("NewSurname")
                .build()

        1 * userRepository.findUserByEmail("oldEmail@gmail.com") >> Optional.of(oldUser)

        when:
        userJpaAdapter.changeUserData("oldEmail@gmail.com", newuser)

        then:
        thrown IncorrectLoginOrPassword
    }

    def "Should add friend request to user by id Pos_TC"(){
        given:
        User alfaUser = User.builder()
                .id(1)
                .name("AlfaUser")
                .surname("Test")
                .password("1111E-!")
                .email("alfa@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        User betaUser = User.builder()
                .id(2)
                .name("BetaUser")
                .surname("Test")
                .password("1111E-!")
                .email("beta@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        1 * userRepository.findUserById(alfaUser.getId()) >> Optional.of(alfaUser)
        1 * userRepository.findUserById(betaUser.getId()) >> Optional.of(betaUser)
        1 * userFriendRepository.getFirstByRequestFromAndRequestTo(alfaUser, betaUser) >> Optional.empty()

        when:
        def result = userJpaAdapter.addFriendRequestToUser(alfaUser.getId(), betaUser.getId())

        then:
        1 * userFriendRepository.save(_ as UserFriend)
        "beta@gmail.com" == result.email
        "BetaUser" == result.name
    }

    def "Should not add friend request to user by id Neg_TC"(){
        given:
        User alfaUser = User.builder()
                .id(1)
                .name("AlfaUser")
                .surname("Test")
                .password("1111E-!")
                .email("alfa@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        User betaUser = User.builder()
                .id(2)
                .name("BetaUser")
                .surname("Test")
                .password("1111E-!")
                .email("beta@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        UserFriend userFriend= new UserFriend()
        userFriend.setId(1)
        userFriend.setRequestFrom(alfaUser)
        userFriend.setRequestTo(betaUser)

        1 * userRepository.findUserById(alfaUser.getId()) >> Optional.of(alfaUser)
        1 * userRepository.findUserById(betaUser.getId()) >> Optional.of(betaUser)
        1 * userFriendRepository.getFirstByRequestFromAndRequestTo(alfaUser, betaUser) >> Optional.of(userFriend)

        when:
        userJpaAdapter.addFriendRequestToUser(alfaUser.getId(), betaUser.getId())

        then:
        0 * userFriendRepository.save(_ as UserFriend)
    }

    def "Should get all friend request by user id Pos_TC"(){
        given:
        User alfaUser = User.builder()
                .id(1)
                .name("AlfaUser")
                .surname("Test")
                .password("1111E-!")
                .email("alfa@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        User betaUser = User.builder()
                .id(2)
                .name("BetaUser")
                .surname("Test")
                .password("1111E-!")
                .email("beta@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        UserFriend userFriend= new UserFriend()
        userFriend.setId(1)
        userFriend.setRequestFrom(alfaUser)
        userFriend.setRequestTo(betaUser)

        List<UserFriend> userFriendList = new ArrayList<>()
        userFriendList.add(userFriend)

        1 * userFriendRepository.getAllByRequestToId(betaUser.getId()) >> userFriendList

        when:
        def listOfUsers = userJpaAdapter.getAllFriendRequestByUserId(betaUser.getId())

        then:
        1 == listOfUsers.size()
        ["alfa@gmail.com"] == listOfUsers*.email
        ["AlfaUser"] == listOfUsers*.name
    }

    def "Should accept friend request to user Pos_TC"(){
        given:
        User alfaUser = User.builder()
                .id(1)
                .name("AlfaUser")
                .surname("Test")
                .password("1111E-!")
                .email("alfa@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        User betaUser = User.builder()
                .id(2)
                .name("BetaUser")
                .surname("Test")
                .password("1111E-!")
                .email("beta@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        UserFriend userFriend = new UserFriend()
        userFriend.setId(1)
        userFriend.setRequestFrom(betaUser)
        userFriend.setRequestTo(alfaUser)

        1 * userRepository.findUserById(alfaUser.getId()) >> Optional.of(alfaUser)
        1 * userRepository.findUserById(betaUser.getId()) >> Optional.of(betaUser)
        2 * userFriendRepository.getFirstByRequestFromAndRequestTo(_ as User, _ as User) >> Optional.of(userFriend)

        when:
        userJpaAdapter.addFriendToUser(alfaUser.getId(), betaUser.getId())

        then:
        2 * userFriendRepository.save(_ as UserFriend)
    }

    def "Should not accept friend request to user Neg_TC"(){
        given:
        User alfaUser = User.builder()
                .id(1)
                .name("AlfaUser")
                .surname("Test")
                .password("1111E-!")
                .email("alfa@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        User betaUser = User.builder()
                .id(2)
                .name("BetaUser")
                .surname("Test")
                .password("1111E-!")
                .email("beta@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        UserFriend userFriend = new UserFriend()
        userFriend.setId(1)
        userFriend.setRequestFrom(betaUser)
        userFriend.setRequestTo(alfaUser)

        1 * userRepository.findUserById(alfaUser.getId()) >> Optional.of(alfaUser)
        1 * userRepository.findUserById(betaUser.getId()) >> Optional.of(betaUser)
        1 * userFriendRepository.getFirstByRequestFromAndRequestTo(_ as User, _ as User) >> Optional.empty()

        when:
        userJpaAdapter.addFriendToUser(alfaUser.getId(), betaUser.getId())

        then:
        0 * userFriendRepository.save(_ as UserFriend)
    }

    def "Should get all friends by user id Pos_TC"(){
        given:
        User alfaUser = User.builder()
                .id(1)
                .name("AlfaUser")
                .surname("Test")
                .password("1111E-!")
                .email("alfa@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        User betaUser = User.builder()
                .id(2)
                .name("BetaUser")
                .surname("Test")
                .password("1111E-!")
                .email("beta@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        UserFriend userFriend= new UserFriend()
        userFriend.setId(1)
        userFriend.setRequestAccepted(true)
        userFriend.setRequestFrom(alfaUser)
        userFriend.setRequestTo(betaUser)

        List<UserFriend> userFriendList = new ArrayList<>()
        userFriendList.add(userFriend)

        1 * userFriendRepository.getAllByRequestFromId(betaUser.getId()) >> userFriendList

        when:
        def listOfUsers = userJpaAdapter.getAllFriendsByUserId(betaUser.getId())

        then:
        1 == listOfUsers.size()
        ["beta@gmail.com"] == listOfUsers*.email
        ["BetaUser"] == listOfUsers*.name
    }
    def "Should remove friend from user by id Pos_TC"(){
        given:
        User alfaUser = User.builder()
                .id(1)
                .name("AlfaUser")
                .surname("Test")
                .password("1111E-!")
                .email("alfa@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        User betaUser = User.builder()
                .id(2)
                .name("BetaUser")
                .surname("Test")
                .password("1111E-!")
                .email("beta@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        UserFriend userFriend = new UserFriend()
        userFriend.setId(1)
        userFriend.setRequestAccepted(true)
        userFriend.setRequestFrom(betaUser)
        userFriend.setRequestTo(alfaUser)

        1 * userRepository.findUserById(alfaUser.getId()) >> Optional.of(alfaUser)
        1 * userRepository.findUserById(betaUser.getId()) >> Optional.of(betaUser)
        4 * userFriendRepository.getFirstByRequestFromAndRequestTo(_ as User, _ as User) >> Optional.of(userFriend)

        when:
        userJpaAdapter.removeFriendFromUser(alfaUser.getId(), betaUser.getId())

        then:
        2 * userFriendRepository.delete(_ as UserFriend)
    }

    def "Should remove friend from user by id Neg_TC"(){
        given:
        User alfaUser = User.builder()
                .id(1)
                .name("AlfaUser")
                .surname("Test")
                .password("1111E-!")
                .email("alfa@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        User betaUser = User.builder()
                .id(2)
                .name("BetaUser")
                .surname("Test")
                .password("1111E-!")
                .email("beta@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        UserFriend userFriend = new UserFriend()
        userFriend.setId(1)
        userFriend.setRequestAccepted(true)
        userFriend.setRequestFrom(betaUser)
        userFriend.setRequestTo(alfaUser)

        1 * userRepository.findUserById(alfaUser.getId()) >> Optional.of(alfaUser)
        1 * userRepository.findUserById(betaUser.getId()) >> Optional.of(betaUser)
        2 * userFriendRepository.getFirstByRequestFromAndRequestTo(_ as User, _ as User) >> Optional.empty()

        when:
        userJpaAdapter.removeFriendFromUser(alfaUser.getId(), betaUser.getId())

        then:
        0 * userFriendRepository.delete(_ as UserFriend)
    }

    def "Should return friend data by friend id Pos_TC"(){
        given:
        User alfaUser = User.builder()
                .id(1)
                .name("AlfaUser")
                .surname("Test")
                .password("1111E-!")
                .email("alfa@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        User betaUser = User.builder()
                .id(2)
                .name("BetaUser")
                .surname("Test")
                .password("1111E-!")
                .email("beta@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        UserFriend userFriend = new UserFriend()
        userFriend.setId(1)
        userFriend.setRequestAccepted(true)
        userFriend.setRequestFrom(betaUser)
        userFriend.setRequestTo(alfaUser)

        2 * userRepository.findUserById(betaUser.getId()) >> Optional.of(betaUser)
        1 * userRepository.findUserById(alfaUser.getId()) >> Optional.of(alfaUser)
        1 * userFriendRepository.getFirstByRequestFromAndRequestTo(_ as User, _ as User) >> Optional.of(userFriend)

        when:
        def result = userJpaAdapter.getFriendDataByFriendId(alfaUser.getId(), betaUser.getId())

        then:
        "accepted" == result.getAction()
    }

    def "Should return exception UserNotFoundException in getFriendDataByFriendId Neg_TC"(){
        given:
        User alfaUser = User.builder()
                .id(1)
                .name("AlfaUser")
                .surname("Test")
                .password("1111E-!")
                .email("alfa@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        User betaUser = User.builder()
                .id(2)
                .name("BetaUser")
                .surname("Test")
                .password("1111E-!")
                .email("beta@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        UserFriend userFriend = new UserFriend()
        userFriend.setId(1)
        userFriend.setRequestAccepted(true)
        userFriend.setRequestFrom(betaUser)
        userFriend.setRequestTo(alfaUser)

        1 * userRepository.findUserById(betaUser.getId()) >> Optional.empty()

        when:
        userJpaAdapter.getFriendDataByFriendId(alfaUser.getId(), betaUser.getId())

        then:
        thrown UserNotFoundException
    }

    def "Should return friend data by friend id acction = pending_your_approval Pos_TC"(){
        given:
        User alfaUser = User.builder()
                .id(1)
                .name("AlfaUser")
                .surname("Test")
                .password("1111E-!")
                .email("alfa@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        User betaUser = User.builder()
                .id(2)
                .name("BetaUser")
                .surname("Test")
                .password("1111E-!")
                .email("beta@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        UserFriend userFriend = new UserFriend()
        userFriend.setId(1)
        userFriend.setRequestAccepted(true)
        userFriend.setRequestFrom(betaUser)
        userFriend.setRequestTo(alfaUser)

        2 * userRepository.findUserById(betaUser.getId()) >> Optional.of(betaUser)
        1 * userRepository.findUserById(alfaUser.getId()) >> Optional.of(alfaUser)
        1 * userFriendRepository.getFirstByRequestFromAndRequestTo(alfaUser, betaUser) >> Optional.empty()
        1 * userFriendRepository.getFirstByRequestFromAndRequestTo(betaUser, alfaUser) >> Optional.of(userFriend)

        when:
        def result = userJpaAdapter.getFriendDataByFriendId(alfaUser.getId(), betaUser.getId())

        then:
        "pending_your_approval" == result.getAction()
    }

    def "Should return friend data by friend id acction = not_friend Pos_TC"(){
        given:
        User alfaUser = User.builder()
                .id(1)
                .name("AlfaUser")
                .surname("Test")
                .password("1111E-!")
                .email("alfa@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        User betaUser = User.builder()
                .id(2)
                .name("BetaUser")
                .surname("Test")
                .password("1111E-!")
                .email("beta@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        UserFriend userFriend = new UserFriend()
        userFriend.setId(1)
        userFriend.setRequestAccepted(true)
        userFriend.setRequestFrom(betaUser)
        userFriend.setRequestTo(alfaUser)

        2 * userRepository.findUserById(betaUser.getId()) >> Optional.of(betaUser)
        1 * userRepository.findUserById(alfaUser.getId()) >> Optional.of(alfaUser)
        1 * userFriendRepository.getFirstByRequestFromAndRequestTo(alfaUser, betaUser) >> Optional.empty()
        1 * userFriendRepository.getFirstByRequestFromAndRequestTo(betaUser, alfaUser) >> Optional.empty()

        when:
        def result = userJpaAdapter.getFriendDataByFriendId(alfaUser.getId(), betaUser.getId())

        then:
        "not_friend" == result.getAction()
    }

    def "Should add complaint to photo by id _PosTC"(){
        given:
        def alfaUser = User.builder()
                .id(1)
                .name("AlfaUser")
                .surname("Test")
                .password("1111E-!")
                .email("alfa@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        def photo = UserGallery.builder()
                .user_id(alfaUser)
                .build()

        def photoComplaint = new PhotoComplaint()
        photoComplaint.setUserGallery(photo)

        userGalleryRepository.findById(photo.getId()) >> Optional.of(photo)
        photoComplaintRepository.findByUserGalleryId(photo.getId())  >> Optional.empty()

        when:
        userJpaAdapter.complaintPhoto(photo.getId())

        then:

        1 * photoComplaintRepository.save(_ as PhotoComplaint)
    }

    def "Should not add complaint to photo by id _PosTC"(){
        given:
        def alfaUser = User.builder()
                .id(1)
                .name("AlfaUser")
                .surname("Test")
                .password("1111E-!")
                .email("alfa@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        def photo = UserGallery.builder()
                .user_id(alfaUser)
                .build()

        def photoComplaint = new PhotoComplaint()
        photoComplaint.setUserGallery(photo)

        userGalleryRepository.findById(photo.getId()) >> Optional.of(photo)
        photoComplaintRepository.findByUserGalleryId(photo.getId())  >> Optional.of(photoComplaint)

        when:
        userJpaAdapter.complaintPhoto(photo.getId())

        then:

        0 * photoComplaintRepository.save(_ as PhotoComplaint)
    }

    def "Should delete photo with compliant by photo id _PosTC"(){
        def photo = UserGallery.builder()
                .build()

        def photoComplaint = new PhotoComplaint()
        photoComplaint.setUserGallery(photo)

        photoComplaintRepository.findByUserGalleryId(photo.getId())  >> Optional.of(photoComplaint)

        when:
        userJpaAdapter.deletePhotoWithComplaint(photo.getId())

        then:
        1 * userGalleryRepository.deleteById(photo.getId())
    }

    def "Should not delete photo with compliant by photo id _PosTC"(){
        def photo = UserGallery.builder()
                .build()

        def photoComplaint = new PhotoComplaint()
        photoComplaint.setUserGallery(photo)

        photoComplaintRepository.findByUserGalleryId(photo.getId())  >> Optional.empty()

        when:
        userJpaAdapter.deletePhotoWithComplaint(photo.getId())

        then:
        0 * userGalleryRepository.deleteById(photo.getId())
    }

}
