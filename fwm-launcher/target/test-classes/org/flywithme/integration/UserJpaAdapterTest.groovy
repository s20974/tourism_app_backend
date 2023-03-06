package org.flywithme.integration

import org.flywithme.adapters.UserJpaAdapter
import org.flywithme.data.enums.Gender
import org.flywithme.data.enums.Status
import org.flywithme.data.user.UserDto
import org.flywithme.data.user.UserRegisterDto
import org.flywithme.entity.PhotoComplaint
import org.flywithme.entity.User
import org.flywithme.entity.UserFriend
import org.flywithme.entity.UserGallery
import org.flywithme.repository.PhotoComplaintRepository
import org.flywithme.repository.UserFriendRepository
import org.flywithme.repository.UserGalleryRepository
import org.flywithme.repository.UserRepository
import org.flywithme.test.AcceptanceBaseSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserJpaAdapterTest extends AcceptanceBaseSpec{

    @Autowired UserRepository userRepository

    @Autowired UserJpaAdapter userJpaAdapter

    @Autowired
    UserFriendRepository userFriendRepository

    @Autowired
    UserGalleryRepository userGalleryRepository

    @Autowired
    PhotoComplaintRepository photoComplaintRepository

    def cleanup() {
        userFriendRepository.deleteAll()
        photoComplaintRepository.deleteAll()
        userGalleryRepository.deleteAll()
        userRepository.deleteAll()
    }

    def setup() {
        User user = User.builder()
                .name("TestUser")
                .surname("Test")
                .password("1111E-!")
                .email("setup@gmail.com")
                .emailConfirmed(true)
                .build()
        userRepository.save(user)

        User alfaUser = User.builder()
                .name("AlfaUser")
                .surname("Test")
                .password("1111E-!")
                .email("alfa@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()
        userRepository.save(alfaUser)

        User betaUser = User.builder()
                .name("BetaUser")
                .surname("Test")
                .password("1111E-!")
                .email("beta@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()
        userRepository.save(betaUser)

        User gammaUser = User.builder()
                .name("GammaUser")
                .surname("Test")
                .password("1111E-!")
                .email("gamma@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()
        userRepository.save(gammaUser)

        User deltaUser = User.builder()
                .name("DeltaUser")
                .surname("Test")
                .password("1111E-!")
                .email("delta@gmail.com")
                .emailConfirmed(true)
                .status(Status.NO_WANTING)
                .gender(Gender.MALE)
                .country("Poland")
                .build()
        userRepository.save(deltaUser)

        UserFriend userFriend = new UserFriend();
        userFriend.setRequestTo(gammaUser)
        userFriend.setRequestFrom(alfaUser)
        userFriendRepository.save(userFriend)

        UserFriend userFriend2 = new UserFriend();
        userFriend2.setRequestFrom(alfaUser)
        userFriend2.setRequestTo(deltaUser)
        userFriend2.setRequestAccepted(true)
        userFriendRepository.save(userFriend2)

        def photo = UserGallery.builder()
                .user_id(user)
                .build()
        userGalleryRepository.save(photo)

        def photoComplaint = new PhotoComplaint()
        photoComplaint.setUserGallery(photo)
        photoComplaintRepository.save(photoComplaint)

    }

    def 'Should register new user _PosTC'() {
        given:
        def userDto = UserRegisterDto.builder()
                .email("test@gmail.com")
                .password("A90210edfyz-")
                .name("Name")
                .surname("Surname")
                .build()

        when:
        userJpaAdapter.addUser(userDto)
        def user = userRepository.findUserByEmail('test@gmail.com')
        def verifyCode = user.get().getVerificationCode()

        then:
        user.isPresent()
        'test@gmail.com' == user.get().getEmail()
        'Name' == user.get().getName()
        'Surname' == user.get().getSurname()
        !user.get().isEmailConfirmed()

        when: 'Should verify user email'
        def response = userJpaAdapter.verifyEmail(verifyCode)
        def user2 = userRepository.findUserByEmail('test@gmail.com')

        then:
        response
        user2.get().isEmailConfirmed()
    }

    def "Should return user data by email _PosTC"() {
        given:
        def email = 'setup@gmail.com'

        when:
        def user = userJpaAdapter.getUserInformationByEmail(email)

        then:
        user.email == email
        user.name == 'TestUser'
        user.surname == 'Test'
    }

    def "Should update user data by email _PosTC"() {
        given:
        def newUserDto = UserDto.builder()
                .email("newEmail@gmail.com")
                .password("A90210edfyz-")
                .name("Name")
                .surname("Surname")
                .build()
        def setupEmail = 'setup@gmail.com'

        when:
        def user = userJpaAdapter.changeUserData(setupEmail, newUserDto)

        then:
        verifyAll(user){
            email == newUserDto.email
            password == newUserDto.password
            name == newUserDto.name
            surname == newUserDto.surname
        }
    }

    def "Should add friend request to user by id _PosTC"(){
        given:
        def idUser = userRepository.findUserByEmail("alfa@gmail.com").get().id
        def idUser2 = userRepository.findUserByEmail("beta@gmail.com").get().id

        when :
        def user = userJpaAdapter.addFriendRequestToUser(idUser, idUser2)

        then:
        "beta@gmail.com" == user.email
        "BetaUser" == user.name
    }

    def "Should get all friend request by user id _PosTC"(){
        given:
        def idUser2 = userRepository.findUserByEmail("gamma@gmail.com").get().id

        when:
        def listOfUsers = userJpaAdapter.getAllFriendRequestByUserId(idUser2)

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
        userJpaAdapter.addFriendToUser(idUser2, idUser)

        then:
        [true, true] == userFriendRepository.getAllByRequestFromId(idUser)*.requestAccepted
    }

    def "Should get all accepted friends by user id _PosTC"(){
        given:
        def idUser = userRepository.findUserByEmail("alfa@gmail.com").get().id

        when:
        def listOfUsers = userJpaAdapter.getAllFriendsByUserId(idUser)

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
        userJpaAdapter.removeFriendFromUser(idUser, idUser2)

        then:
        userFriendRepository.getFirstByRequestFromAndRequestTo(user, user2).isEmpty()
    }

    def "Should get friend data by friend id _PosTC"(){
        given :
        def idUser = userRepository.findUserByEmail("alfa@gmail.com").get().getId()
        def idUser2 = userRepository.findUserByEmail("delta@gmail.com").get().getId()

        when:
        def friend = userJpaAdapter.getFriendDataByFriendId(idUser, idUser2)

        then:
        "delta@gmail.com" == friend.getEmail()
        "DeltaUser" == friend.getName()
    }

    def "Should get users by filter _PosTC"(){
        given:
        def status = Status.FOUND
        def gender = Gender.MALE
        def country = "Poland"
        def pegable = new PageRequest(0, 30, Sort.by("name"))

        when:
        def filteredUsers = userJpaAdapter.getUsersDataByFilter(country, gender.toString(), status.toString(), pegable)

        then:
        3 == filteredUsers.size()
        ["FOUND", "FOUND", "FOUND"] == filteredUsers*.getStatus()*.toString()
        ["MALE", "MALE", "MALE"] == filteredUsers*.getGender()*.toString()
        ["Poland", "Poland", "Poland"] == filteredUsers*.getCountry()
    }

    def "Should add complain to photo by id _PosTc"(){
        given:
        def id = userGalleryRepository.findAll().first().getId()

        when:
        userJpaAdapter.complaintPhoto(id)

        then:
        1 == photoComplaintRepository.findAll().size()
    }

    def "Should delete photo with complain _PosTc"(){
        given:
        def id = userGalleryRepository.findAll().first().getId()

        when:
        userJpaAdapter.deletePhotoWithComplaint(id)

        then:
        0 == userGalleryRepository.findAll().size()
    }
}
