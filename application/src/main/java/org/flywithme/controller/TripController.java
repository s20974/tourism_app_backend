package org.flywithme.controller;

import lombok.RequiredArgsConstructor;
import org.flywithme.data.trip.FilteredTripDto;
import org.flywithme.data.trip.TripDto;
import org.flywithme.ports.api.TripServicePort;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000",  allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class TripController {

    private final TripServicePort tripServicePort;

    @PostMapping("/add")
    public boolean addTrip(@RequestBody TripDto tripDto){
        return tripServicePort.addTrip(tripDto);
    }

    @GetMapping
    public List<FilteredTripDto> getTrips(@RequestParam(name = "country") String country,
                                          @RequestParam(name = "from", required = false) String from,
                                          @RequestParam(name = "to", required = false) String dateTo,
                                          @RequestParam(name = "max", required = false) Integer max,
                                          Pageable pageable){
        return tripServicePort.findTrips(country, from, dateTo, max, pageable);
    }

    @PostMapping("/join")
    public FilteredTripDto joinToTrip(@RequestParam("userId") Long userId, @RequestParam("tripId") Long tripId){
        return tripServicePort.joinToTrip(userId, tripId);
    }

    @GetMapping("/byUserId")
    public List<FilteredTripDto> getTripsByUserId(@RequestParam(name = "id") Long userId){
        return tripServicePort.getUserId(userId);
    }
}
