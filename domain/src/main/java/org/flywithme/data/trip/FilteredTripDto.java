package org.flywithme.data.trip;

import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilteredTripDto {

    private Long id;

    private String country;

    private Date dateFrom;

    private Date dateTo;

    private Integer maxSize;

    private Set<JoinedUser> joinedUsers = new HashSet<>();

    private String header;

    private String description;
}
