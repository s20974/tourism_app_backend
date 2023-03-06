package org.flywithme.acceptance

import org.flywithme.data.trip.TripDto
import org.flywithme.entity.User
import org.flywithme.repository.TripRepository
import org.flywithme.repository.UserRepository
import org.flywithme.test.AcceptanceBaseSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TripControllerTest extends AcceptanceBaseSpec{

    @Autowired
    private TripRepository tripRepository

    @Autowired
    private UserRepository userRepository

    def cleanup() {
        tripRepository.deleteAll()
        userRepository.deleteAll()
    }

    def 'Should add new trip _PosTC'() {
        given:
        User user = User.builder()
                .name("TestUser")
                .surname("Test")
                .password("1111E-!")
                .email("setup@gmail.com")
                .emailConfirmed(true)
                .build()
        User user1 = userRepository.save(user)
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date from = formatter.parse("2022-10-20")
        Date to = formatter.parse("2022-10-25")
        def tripDto = TripDto.builder()
            .header("Header")
            .country("Poland")
            .dateFrom(from)
            .dateTo(to)
            .maxPeople(3)
            .userId(user1.id)
            .build()

        when:
        restTemplate.exchange("$BASE_URL:$PORT/api/v1/trip/add", HttpMethod.POST, createHttpEntity(tripDto), Boolean.class)
        def trip = tripRepository.findAll()

        then:
        1 == trip.size()
        ["Poland"] == trip*.country
        ["Header"] == trip*.header
    }
}
