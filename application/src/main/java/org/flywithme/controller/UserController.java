package org.flywithme.controller;

import lombok.RequiredArgsConstructor;
import org.flywithme.data.posts.PostCommentDto;
import org.flywithme.data.user.UserDto;
import org.flywithme.data.user.UserGalleryDto;
import org.flywithme.data.user.UserPhotoDto;
import org.flywithme.data.user.UserRegisterDto;
import org.flywithme.ports.api.UserServicePort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000",  allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserServicePort userServicePort;

    @PostMapping("/register")
    public void addUser(@Valid @RequestBody UserRegisterDto register) {
        userServicePort.addUser(register);
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam("code") String url){
        return userServicePort.verifyEmail(url);
    }

    @GetMapping("/getData")
    public UserDto getUserByEmail(@RequestParam("email") String email){
        return userServicePort.getUserInformationByEmail(email);
    }

    @PutMapping("/mainPhoto")
    public UserDto uploadMainPhoto(@RequestParam("email") String email, @RequestPart MultipartFile multipartFile){
        return userServicePort.uploadMainPhoto(email, multipartFile);
    }

    @GetMapping("/userGallery/{id}")
    public List<UserGalleryDto> userGalleryDto(@PathVariable(name = "id") Long userId){
        return userServicePort.getUserGallery(userId);
    }

    @PutMapping("/addToGallery")
    public UserDto uploadPhotoToGallery(@RequestParam("email") String email, @RequestPart MultipartFile multipartFile){
        return userServicePort.uploadPhotoToGallery(email, multipartFile);
    }

    @PutMapping("/userUpdate")
    public UserDto changeUserDataByEmail(@RequestParam("email") String email, @RequestBody UserDto userDto){
        return userServicePort.changeUserData(email,userDto);
    }

    @GetMapping("/like/{photoId}/{userId}")
    public void likePhoto(@PathVariable(name = "photoId") Long photoId, @PathVariable(name = "userId") Long userId){
        userServicePort.likePhoto(photoId, userId);
    }

    @PostMapping("/comment")
    public PostCommentDto addComment(@RequestBody PostCommentDto postCommentDto){
        return userServicePort.addComment(postCommentDto);
    }

    @DeleteMapping("/photo/{id}")
    public void deletePhoto(@PathVariable("id") Long id){
        userServicePort.deletePhoto(id);
    }

    @GetMapping("/photo/{id}")
    public UserPhotoDto getUserGalleryDetails(@PathVariable(name = "id") Long photoId){
        return userServicePort.getUserGalleryDetails(photoId);
    }
}
