package org.flywithme.data.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;

    @NotNull
    @Size(min = 4, max = 255)
    private String name;

    @NotNull
    @Size(min = 4, max = 255)
    private String surname;

    @NotNull
    @Size(min = 4, max = 255)
    @Pattern(regexp = ".+@.+\\..+")
    private String email;

    @NotNull
    @Size(min = 4, max = 255)
    @Pattern(regexp = "^.*(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^\\-&+=]).*$")
    private String password;

    private String oldPassword;

    private String phoneNumber;

    private String gender;

    private String status;

    private String country;

    private String city;

    private List<String> userGalleries;

    private String mainPhotoUrl;

    private Long numberOfPosts;

    private Long numberOfFriends;
}
