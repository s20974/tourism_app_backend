package org.flywithme.data.mainpage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PhotoMainPageDto {

    private Long photoId;

    private String userMainPhoto;

    private Long userId;

    private String photoUrl;
}
