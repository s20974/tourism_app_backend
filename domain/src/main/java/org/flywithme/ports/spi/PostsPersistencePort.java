package org.flywithme.ports.spi;

import org.flywithme.data.posts.NewPostDto;
import org.flywithme.data.posts.PostCommentDto;
import org.flywithme.data.posts.PostDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostsPersistencePort {

    boolean newPost(NewPostDto newPostDto, MultipartFile multipartFile);

    void deletePost(Long id);

    PostDto getPost(Long id);

    List<PostDto> getAllPostsByUserId(Long id);

    void like(Long photoId, Long userId);

    PostCommentDto addComment(PostCommentDto postCommentDto);

    void complaintPost(Long postId);

    void deletePostWithComplaint(Long postId);

    void deleteComplaint(Long postId);

    List<PostDto> getAllPostComplaint(Pageable pageable);
}
