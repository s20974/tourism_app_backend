package org.flywithme.adapters;

import com.amazonaws.services.apigateway.model.Op;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.flywithme.adapters.specifications.TripSpecifications;
import org.flywithme.data.trip.FilteredTripDto;
import org.flywithme.data.trip.JoinedUser;
import org.flywithme.data.trip.TripDto;
import org.flywithme.entity.Trip;
import org.flywithme.entity.User;
import org.flywithme.ports.spi.TripPersistencePort;
import org.flywithme.repository.TripRepository;
import org.flywithme.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
@NoArgsConstructor
public class TripJpaAdapter implements TripPersistencePort {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    Mapper mapper = DozerBeanMapperBuilder.buildDefault();

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public boolean addTrip(TripDto tripDto) {
        User user = userRepository.findById(tripDto.getUserId()).get();
        var trip = tripDtoToTrip(tripDto);
        trip.setUser(user);
        tripRepository.save(trip);
        return true;
    }

    @SneakyThrows
    @Override
    public List<FilteredTripDto> findTrips(String country, String from, String dateTo, Integer max, Pageable pageable) {
        var dateFrom = Optional.ofNullable(from);
        var to = Optional.ofNullable(dateTo);
        var maxSize = Optional.ofNullable(max);
        var trips = tripRepository.findAll(TripSpecifications.getTripByFilter(country, dateFrom, to, maxSize), pageable);
        return trips.stream().map(this::tripToFilteredDto).toList();
    }

    @Override
    public FilteredTripDto joinToTrip(Long userId, Long tripId) {
       var trip = tripRepository.findById(tripId).get();
       var user = userRepository.findUserById(userId).get();
       if (trip.getListOfJoinedUsers().size() < trip.getMaxPeople()){
           trip.getListOfJoinedUsers().add(user);
           user.setCurrentTrip(trip);
           tripRepository.save(trip);
           userRepository.save(user);
       }
      var filteredTripDto = tripToFilteredDto(trip);
      filteredTripDto.getJoinedUsers().add(new JoinedUser(user.getId(), user.getMainPhotoUrl()));
        return filteredTripDto;

    }

    @Override
    public List<FilteredTripDto> getTripByUserId(Long userId) {
        return tripRepository.findAllByUserId(userId)
                .stream()
                .map(this::tripToFilteredDto)
                .toList();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void deleteDelayedTrips(){
        var now = new Date();
        var formatted = formatter.format(now);
        tripRepository.deleteAllByDateFromLessThan(formatted);
    }


    @SneakyThrows
    public FilteredTripDto tripToFilteredDto(Trip trip){
        return FilteredTripDto.builder()
                .id(trip.getId())
                .country(trip.getCountry())
                .dateFrom(formatter.parse(trip.getDateFrom()))
                .dateTo(formatter.parse(trip.getDateTo()))
                .maxSize(trip.getMaxPeople())
                .header(trip.getHeader())
                .description(trip.getDescription())
                .joinedUsers(getList(trip))
                .build();
    }

    private Trip tripDtoToTrip(TripDto tripDto){
        return Trip.builder()
                .dateFrom(formatter.format(tripDto.getDateFrom()))
                .dateTo(formatter.format(tripDto.getDateTo()))
                .country(tripDto.getCountry())
                .description(tripDto.getDescription())
                .header(tripDto.getHeader())
                .maxPeople(tripDto.getMaxPeople())
                .build();
    }

    private Date parseDate(String date) throws ParseException {
        return formatter.parse(date);
    }

    private Set<JoinedUser> getList(Trip trip){
        Set<JoinedUser> set = new HashSet<>();
        set.add(new JoinedUser(trip.getUser().getId(), trip.getUser().getMainPhotoUrl()));
        trip.getListOfJoinedUsers().forEach(user -> {
            set.add(new JoinedUser(user.getId(), user.getMainPhotoUrl()));
        });
        return set;
    }
}
