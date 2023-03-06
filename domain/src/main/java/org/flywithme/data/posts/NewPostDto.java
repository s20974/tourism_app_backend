package org.flywithme.data.posts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewPostDto {

    @NotNull
    private String userEmail;

    @NotNull
    private String header;

    private String photoUrl;

    private String description;

    private String geoLocation;
}
