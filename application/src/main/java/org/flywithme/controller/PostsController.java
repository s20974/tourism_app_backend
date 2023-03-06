package org.flywithme.controller;

import lombok.RequiredArgsConstructor;
import org.flywithme.data.posts.NewPostDto;
import org.flywithme.data.posts.PostCommentDto;
import org.flywithme.data.posts.PostDto;
import org.flywithme.ports.api.PostsServicePort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000",  allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostsController {

    private final PostsServicePort postsServicePort;

    @PostMapping("/addPost")
    public boolean addPost(@RequestPart("newPostDto") NewPostDto newPostDto, @RequestPart("multipartFile") MultipartFile multipartFile){
        return postsServicePort.newPost(newPostDto, multipartFile);
    }

    @DeleteMapping("/delete/{id}")
    public void deletePost(@PathVariable(name = "id") Long id){
        postsServicePort.deletePost(id);
    }

    @GetMapping("/getPostData/{id}")
    public PostDto getPost(@PathVariable(name = "id") Long id){
        return postsServicePort.getPost(id);
    }

    @GetMapping("/getAllPostByUserId/{id}")
    public List<PostDto> getPostsByUserId(@PathVariable(name = "id") Long id){
        return postsServicePort.getAllPostsByUserId(id);
    }

    @GetMapping("/like/{photoId}/{userId}")
    public void likePost(@PathVariable(name = "photoId") Long photoId, @PathVariable(name = "userId") Long userId){
        postsServicePort.like(photoId, userId);
    }

    @PostMapping("/comment")
    public PostCommentDto addComment(@RequestBody PostCommentDto postCommentDto){
        return postsServicePort.addComment(postCommentDto);
    }
}
