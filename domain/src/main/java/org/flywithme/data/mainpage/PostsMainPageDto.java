package org.flywithme.data.mainpage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flywithme.data.posts.PostDto;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PostsMainPageDto {

    private Long postId;

    private String userPhotoUrl;

    private Long userId;

    private String postPhotoUrl;

    private String header;

    private String description;

    private String geoLocation;
}
