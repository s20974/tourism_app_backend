package org.flywithme.ports.api;

import org.flywithme.data.trip.FilteredTripDto;
import org.flywithme.data.trip.TripDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TripServicePort {

    boolean addTrip(TripDto tripDto);

    List<FilteredTripDto> findTrips(String country, String from, String dateTo, Integer max, Pageable pageable);

    FilteredTripDto joinToTrip(Long userId, Long tripId);

    List<FilteredTripDto> getUserId(Long userId);
}
