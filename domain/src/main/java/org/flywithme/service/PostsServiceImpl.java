package org.flywithme.service;

import lombok.RequiredArgsConstructor;
import org.flywithme.data.posts.NewPostDto;
import org.flywithme.data.posts.PostCommentDto;
import org.flywithme.data.posts.PostDto;
import org.flywithme.ports.api.PostsServicePort;
import org.flywithme.ports.spi.PostsPersistencePort;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
public class PostsServiceImpl implements PostsServicePort {

    private final PostsPersistencePort postsPersistencePort;

    @Override
    public boolean newPost(NewPostDto newPostDto, MultipartFile multipartFile) {
        return postsPersistencePort.newPost(newPostDto, multipartFile);
    }

    @Override
    public void deletePost(Long id) {
        postsPersistencePort.deletePost(id);
    }

    @Override
    public PostDto getPost(Long id) {
        return postsPersistencePort.getPost(id);
    }

    @Override
    public List<PostDto> getAllPostsByUserId(Long id) {
        return postsPersistencePort.getAllPostsByUserId(id);
    }

    @Override
    public void like(Long photoId, Long userId) {
        postsPersistencePort.like(photoId, userId);
    }

    @Override
    public PostCommentDto addComment(PostCommentDto postCommentDto) {
        return postsPersistencePort.addComment(postCommentDto);
    }

    @Override
    public void complaintPost(Long postId) {
        postsPersistencePort.complaintPost(postId);
    }

    @Override
    public void deletePostWithComplaint(Long postId) {
        postsPersistencePort.deletePostWithComplaint(postId);
    }

    @Override
    public void deleteComplaint(Long postId) {
        postsPersistencePort.deleteComplaint(postId);
    }

    @Override
    public List<PostDto> getAllPostComplaint(Pageable pageable) {
        return postsPersistencePort.getAllPostComplaint(pageable);
    }
}
