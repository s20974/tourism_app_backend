package org.flywithme.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity(name = "trip_tb")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dateFrom;

    private String dateTo;

    private String country;

    private String header;

    private String description;

    private Integer maxPeople;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @OneToMany(mappedBy = "currentTrip")
    private Set<User> listOfJoinedUsers;


}
