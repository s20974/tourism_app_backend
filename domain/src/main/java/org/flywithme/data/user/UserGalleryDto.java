package org.flywithme.data.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserGalleryDto {

    private Long id;

    private String url;

    private Long likes;

    private Long comments;
}
