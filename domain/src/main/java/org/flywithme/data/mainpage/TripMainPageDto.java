package org.flywithme.data.mainpage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flywithme.data.trip.JoinedUser;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TripMainPageDto {

    private Long tripId;

    private Long userId;

    private String userMainPhoto;

    private String dateFrom;

    private String dateTo;

    private String country;

    private String header;

    private String description;

    private Integer maxPeople;

    private Set<JoinedUser> joinedUsers = new HashSet<>();
}
