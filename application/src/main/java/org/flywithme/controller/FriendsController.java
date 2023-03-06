package org.flywithme.controller;

import lombok.RequiredArgsConstructor;
import org.flywithme.data.FriendDto;
import org.flywithme.data.enums.Gender;
import org.flywithme.data.enums.Status;
import org.flywithme.data.filters.FriendsFilter;
import org.flywithme.data.mainpage.PhotoMainPageDto;
import org.flywithme.data.mainpage.PostsMainPageDto;
import org.flywithme.data.mainpage.TripMainPageDto;
import org.flywithme.data.user.UserDto;
import org.flywithme.data.user.UserFilterDto;
import org.flywithme.ports.api.UserServicePort;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000",  allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendsController {

    private final UserServicePort userServicePort;

    @GetMapping("/addFriend")
    public UserDto addFriendToUser(@RequestParam("userId") Long userId, @RequestParam("friendId") Long friendId){
        return userServicePort.addFriendToUser(userId, friendId);
    }

    @GetMapping("/getAllFriends")
    public List<FriendDto> getAllFriendsByUserId(@RequestParam("id") Long id){
        return userServicePort.getAllFriendsByUserId(id);
    }

    @DeleteMapping("/deleteFriend")
    public UserDto removeFriendFromUser(@RequestParam("userId") Long userId, @RequestParam("friendId") Long friendId){
        return userServicePort.removeFriendFromUser(userId, friendId);
    }

    @GetMapping("/addFriendRequest")
    public UserDto addFriendRequestToUser(@RequestParam("userId") Long userId, @RequestParam("friendId") Long friendId){
        return userServicePort.addFriendRequestToUser(userId, friendId);
    }

    @GetMapping("/getAllFriendRequest")
    public List<FriendDto> getAllFriendRequestByUserId(@RequestParam("id") Long id){
        return userServicePort.getAllFriendRequestByUserId(id);
    }

    @GetMapping("/getFriendData")
    public FriendDto getFriendDataByFriendId(@RequestParam("userId") Long userId, @RequestParam("friendId") Long friendId){
        return userServicePort.getFriendDataByFriendId(userId, friendId);
    }

    @GetMapping("/getUsers")
    public List<UserFilterDto> getUsersDataByFilter(@RequestParam(name = "country") String country,
            @RequestParam(name = "gender", required = false) String gender,
            @RequestParam(name = "status", required = false) String status, Pageable pageable){
        return userServicePort.getUsersDataByFilter(country, gender, status, pageable);
    }

    @GetMapping("/mainPage/posts")
    public List<PostsMainPageDto> getPostsMainPage(@RequestParam(name = "id") Long userId){
        return userServicePort.getPostsMainPage(userId);
    }

    @GetMapping("/mainPage/photo")
    public List<PhotoMainPageDto> getPhotoMainPage(@RequestParam(name = "id") Long userId){
        return userServicePort.getPhotoMainPage(userId);
    }

    @GetMapping("/mainPage/trip")
    public List<TripMainPageDto> getTripMainPage(@RequestParam(name = "id") Long userId){
        return userServicePort.getTripMainPage(userId);
    }
}
