package org.flywithme.ports.spi;

import org.flywithme.data.FriendDto;
import org.flywithme.data.enums.Gender;
import org.flywithme.data.enums.Status;
import org.flywithme.data.filters.FriendsFilter;
import org.flywithme.data.mainpage.PhotoMainPageDto;
import org.flywithme.data.mainpage.PostsMainPageDto;
import org.flywithme.data.mainpage.TripMainPageDto;
import org.flywithme.data.posts.PostCommentDto;
import org.flywithme.data.user.*;
import org.flywithme.exceptions.UserNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserPersistencePort {

    void addUser(UserRegisterDto userDto);

    boolean verifyEmail(String code) throws UserNotFoundException;

    UserDto getUserInformationByEmail(String email);

    UserDto uploadMainPhoto(String email, MultipartFile multipartFile);

    UserDto uploadPhotoToGallery(String email, MultipartFile multipartFile);

    UserDto changeUserData(String email, UserDto userDto);

    UserDto addFriendToUser(Long userId, Long friendId);

    UserDto removeFriendFromUser(Long userId, Long friendId);

    UserDto addFriendRequestToUser(Long userId, Long friendId);

    List<FriendDto> getAllFriendsByUserId(Long id);

    List<FriendDto> getAllFriendRequestByUserId(Long id);

    FriendDto getFriendDataByFriendId(Long userId, Long friendId);

    Optional<String> findUserByEmail(String email);

    List<UserFilterDto> getUsersDataByFilter(String country, String gender, String status, Pageable pageable);

    void like(Long photoId, Long userId);

    PostCommentDto addComment(PostCommentDto postCommentDto);

    void deletePhoto(Long id);

    List<PostsMainPageDto> getPostsMainPage(Long userId);

    List<PhotoMainPageDto> getPhotoMainPage(Long userId);

    List<TripMainPageDto> getTripMainPage(Long userId);

    List<UserGalleryDto> getUserGallery(Long userId);

    UserPhotoDto getUserGalleryDetails(Long photoId);

    void complaintPhoto(Long photoId);

    void deletePhotoWithComplaint(Long photoId);

    void deleteComplaint(Long photoId);

    List<UserGalleryDto> getAllPhotoComplaint(Pageable pageable);
}
