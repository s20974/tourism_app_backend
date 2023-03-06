package org.flywithme.data.trip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TripDto {

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateFrom;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateTo;

    @NotEmpty
    private String country;

    @NotNull
    @Max(value = 10)
    @Min(value = 2)
    private Integer maxPeople;

    @NotNull
    private Long userId;

    @NotNull
    private String header;

    private String description;
}
