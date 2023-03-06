package org.flywithme.mappers;

import javax.annotation.processing.Generated;
import org.flywithme.data.posts.NewPostDto;
import org.flywithme.entity.Post;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-03-06T21:42:52+0100",
    comments = "version: 1.4.2.Final, compiler: Eclipse JDT (IDE) 3.33.0.v20230213-1046, environment: Java 17.0.6 (Eclipse Adoptium)"
)
public class PostMapperImpl implements PostMapper {

    @Override
    public Post newPostDtoToPost(NewPostDto newPostDto) {
        if ( newPostDto == null ) {
            return null;
        }

        Post post = new Post();

        post.setDescription( newPostDto.getDescription() );
        post.setGeoLocation( newPostDto.getGeoLocation() );
        post.setHeader( newPostDto.getHeader() );
        post.setPhotoUrl( newPostDto.getPhotoUrl() );

        return post;
    }
}
