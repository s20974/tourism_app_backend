package org.flywithme.data.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flywithme.data.posts.PostCommentDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPhotoDto {

    private Long id;

    private Long likes;

    private List<PostCommentDto> commentDtos;
}
