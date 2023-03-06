package org.flywithme.data.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterDto {

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
}
