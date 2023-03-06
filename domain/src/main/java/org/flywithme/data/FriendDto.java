package org.flywithme.data;

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
public class FriendDto {

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

    private String country;

    private String city;

    private String mainPhotoUrl;

    // isFriend?, isRequest?, notFriend?, isSendRequest?
    private String action;

    private List<String> userGalleries;
}
