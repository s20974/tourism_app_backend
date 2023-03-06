package org.flywithme.acceptance

import org.flywithme.data.user.UserDto
import org.flywithme.data.user.UserRegisterDto
import org.flywithme.entity.User
import org.flywithme.repository.UserRepository
import org.flywithme.test.AcceptanceBaseSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest extends AcceptanceBaseSpec {

    @Autowired UserRepository userRepository

    def cleanup() {
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
        restTemplate.exchange("$BASE_URL:$PORT/api/v1/user/register", HttpMethod.POST, createHttpEntity(userDto), Void.class)
        def user = userRepository.findUserByEmail('test@gmail.com')
        def verifyCode = user.get().getVerificationCode()

        then:
        user.isPresent()
        'test@gmail.com' == user.get().getEmail()
        'Name' == user.get().getName()
        'Surname' == user.get().getSurname()
        !user.get().isEmailConfirmed()

        when: 'Should verify user email'
        def response = restTemplate.getForObject("$BASE_URL:$PORT/api/v1/user/verify?code=$verifyCode", String.class)
        def user2 = userRepository.findUserByEmail('test@gmail.com')

        then:
        response == 'Email confirmed'
        user2.get().isEmailConfirmed()
    }

    def "Should return user data by email"() {
        given:
        def email = 'setup@gmail.com'

        when:
        def response = restTemplate.getForEntity("$BASE_URL:$PORT/api/v1/user/getData?email=$email", UserDto.class)
        def user = response.getBody()

        then:
        user.email == email
        user.name == 'TestUser'
        user.surname == 'Test'
        user.getNumberOfFriends() == 0
        user.getNumberOfPosts() == 0
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
        def response = restTemplate.exchange("$BASE_URL:$PORT/api/v1/user/userUpdate?email=$setupEmail",
                                                                HttpMethod.PUT, createHttpEntity(newUserDto), UserDto.class)
        def user = response.getBody();

        then:
        verifyAll(user){
            email == newUserDto.email
            password == newUserDto.password
            name == newUserDto.name
            surname == newUserDto.surname
        }
    }
}
