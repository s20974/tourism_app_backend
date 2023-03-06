package org.flywithme.mappers;

import org.flywithme.data.posts.NewPostDto;
import org.flywithme.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    Post newPostDtoToPost(NewPostDto newPostDto);

}
