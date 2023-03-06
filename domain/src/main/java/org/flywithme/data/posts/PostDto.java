package org.flywithme.data.posts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    private Long id;

    private String header;

    private String photoUrl;

    private Long likes;

    private String description;

    private String geoLocation;

    private List<PostCommentDto> comment;
}
