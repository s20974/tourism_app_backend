package org.flywithme.controller;

import lombok.RequiredArgsConstructor;
import org.flywithme.data.posts.PostDto;
import org.flywithme.data.user.UserGalleryDto;
import org.flywithme.ports.api.PostsServicePort;
import org.flywithme.ports.api.UserServicePort;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000",  allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1/complaint")
@RequiredArgsConstructor
public class ComplaintController {

    private final PostsServicePort postsServicePort;
    private final UserServicePort userServicePort;

    @GetMapping("/post")
    public void complaintPost(@RequestParam Long postId){
        postsServicePort.complaintPost(postId);
    }

    @GetMapping("/photo")
    public void complaintPhoto(@RequestParam Long photoId){
        userServicePort.complaintPhoto(photoId);
    }

    @DeleteMapping("/deletePost")
    public void deletePostWithComplaint(@RequestParam Long postId){
        postsServicePort.deletePostWithComplaint(postId);
    }

    @DeleteMapping("/deleteComplaintPost")
    public void deleteComplaintPost(@RequestParam Long postId){
        postsServicePort.deleteComplaint(postId);
    }

    @DeleteMapping("/deletePhoto")
    public void deletePhotoWithComplaint(@RequestParam Long photoId){
        userServicePort.deletePhotoWithComplaint(photoId);
    }

    @DeleteMapping("/deleteComplaintPhoto")
    public void deleteComplaintPhoto(@RequestParam Long photoId){
        userServicePort.deleteComplaint(photoId);
    }

    @GetMapping("/getAllPostComplaint")
    public List<PostDto> getAllPostComplaint(Pageable pageable){
        return postsServicePort.getAllPostComplaint(pageable);
    }

    @GetMapping("/getAllPhotoComplaint")
    public List<UserGalleryDto> getAllPhotoComplaint(Pageable pageable){
        return userServicePort.getAllPhotoComplaint(pageable);
    }
}
