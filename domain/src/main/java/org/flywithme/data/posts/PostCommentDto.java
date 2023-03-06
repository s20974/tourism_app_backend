package org.flywithme.data.posts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentDto {

    @NotEmpty
    private Long id;

    @NotEmpty
    private String content;

    @NotEmpty
    private String authorName;

    @NotEmpty
    private String authorSurname;
}
