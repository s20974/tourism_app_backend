package org.flywithme.service;

import lombok.RequiredArgsConstructor;
import org.flywithme.data.trip.FilteredTripDto;
import org.flywithme.data.trip.TripDto;
import org.flywithme.ports.api.TripServicePort;
import org.flywithme.ports.spi.TripPersistencePort;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class TripServiceImpl implements TripServicePort {

    private final TripPersistencePort tripPersistencePort;

    @Override
    public boolean addTrip(TripDto tripDto) {
        return tripPersistencePort.addTrip(tripDto);
    }

    @Override
    public List<FilteredTripDto> findTrips(String country, String from, String dateTo, Integer max, Pageable pageable) {
        return tripPersistencePort.findTrips(country, from, dateTo, max, pageable);
    }

    @Override
    public FilteredTripDto joinToTrip(Long userId, Long tripId) {
        return tripPersistencePort.joinToTrip(userId, tripId);
    }

    @Override
    public List<FilteredTripDto> getUserId(Long userId) {
        return tripPersistencePort.getTripByUserId(userId);
    }
}
