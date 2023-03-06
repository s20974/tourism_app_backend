package org.flywithme.data.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flywithme.data.enums.Gender;
import org.flywithme.data.enums.Status;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserFilterDto {
    private Long id;

    private String name;

    private String surname;

    private String country;

    private String city;

    private String mainPhotoUrl;

    private Gender gender;

    private Status status;
}
