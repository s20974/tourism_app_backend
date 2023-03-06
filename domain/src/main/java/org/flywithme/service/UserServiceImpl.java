package org.flywithme.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.flywithme.data.FriendDto;
import org.flywithme.data.enums.Gender;
import org.flywithme.data.enums.Status;
import org.flywithme.data.filters.FriendsFilter;
import org.flywithme.data.mainpage.PhotoMainPageDto;
import org.flywithme.data.mainpage.PostsMainPageDto;
import org.flywithme.data.mainpage.TripMainPageDto;
import org.flywithme.data.posts.PostCommentDto;
import org.flywithme.data.user.*;
import org.flywithme.exceptions.IncorrectFileExtension;
import org.flywithme.exceptions.UserAlreadyExistsException;
import org.flywithme.exceptions.UserNotFoundException;
import org.flywithme.ports.api.UserServicePort;
import org.flywithme.ports.spi.UserPersistencePort;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class UserServiceImpl implements UserServicePort {

    private final UserPersistencePort userPersistencePort;

    @SneakyThrows
    @Override
    public void addUser(@Valid UserRegisterDto register) {
        if (!userWithEmailExists(register.getEmail())) {
            userPersistencePort.addUser(register);
        }else {
            throw new UserAlreadyExistsException("User with this email already exists");
        }
    }

    @SneakyThrows
    @Override
    public String verifyEmail(String code) {
        if(userPersistencePort.verifyEmail(code)){
            return "Email confirmed";
        }
        throw new UserNotFoundException("User with this verification code not found");
    }

    @SneakyThrows
    @Override
    public UserDto getUserInformationByEmail(String email) {
        return userPersistencePort.getUserInformationByEmail(email);
    }

    @SneakyThrows
    @Override
    public UserDto uploadMainPhoto(String email, MultipartFile multipartFile) {
        if (validateFileExtensions(multipartFile)) {
            return userPersistencePort.uploadMainPhoto(email, multipartFile);
        } else {
            throw new IncorrectFileExtension("Incorrect file extension");
        }
    }

    @SneakyThrows
    @Override
    public UserDto uploadPhotoToGallery(String email, MultipartFile multipartFile) {
        if (validateFileExtensions(multipartFile)) {
            return userPersistencePort.uploadPhotoToGallery(email, multipartFile);
        } else {
            throw new IncorrectFileExtension("Incorrect file extension");
        }
    }

    @SneakyThrows
    @Override
    public UserDto changeUserData(String email, UserDto userDto) {
        if(!email.equals(userDto.getEmail()) && userWithEmailExists(userDto.getEmail())){
            throw new UserAlreadyExistsException("User with this email already exists");
        }
        return userPersistencePort.changeUserData(email, userDto);
    }

    @Override
    public UserDto addFriendToUser(Long userId, Long friendId) {
        return userPersistencePort.addFriendToUser(userId, friendId);
    }

    @Override
    public UserDto removeFriendFromUser(Long userId, Long friendId) {
        return userPersistencePort.removeFriendFromUser(userId, friendId);
    }

    @Override
    public UserDto addFriendRequestToUser(Long userId, Long friendId) {
        return userPersistencePort.addFriendRequestToUser(userId, friendId);
    }

    @Override
    public List<FriendDto> getAllFriendsByUserId(Long id) {
        return userPersistencePort.getAllFriendsByUserId(id);
    }

    @Override
    public List<FriendDto> getAllFriendRequestByUserId(Long id) {
        return userPersistencePort.getAllFriendRequestByUserId(id);
    }

    @Override
    public FriendDto getFriendDataByFriendId(Long userId, Long friendId) {
        return userPersistencePort.getFriendDataByFriendId(userId, friendId);
    }

    @Override
    public List<UserFilterDto> getUsersDataByFilter(String country, String gender, String status, Pageable pageable) {
        return userPersistencePort.getUsersDataByFilter(country, gender, status, pageable);
    }

    @Override
    public void likePhoto(Long photoId, Long userId) {
        userPersistencePort.like(photoId, userId);
    }

    @Override
    public PostCommentDto addComment(PostCommentDto postCommentDto) {
        return userPersistencePort.addComment(postCommentDto);
    }

    @Override
    public void deletePhoto(Long id) {
        userPersistencePort.deletePhoto(id);
    }

    @Override
    public List<PostsMainPageDto> getPostsMainPage(Long userId) {
        return userPersistencePort.getPostsMainPage(userId);
    }

    @Override
    public List<PhotoMainPageDto> getPhotoMainPage(Long userId) {
        return userPersistencePort.getPhotoMainPage(userId);
    }

    @Override
    public List<TripMainPageDto> getTripMainPage(Long userId) {
        return userPersistencePort.getTripMainPage(userId);
    }

    @Override
    public List<UserGalleryDto> getUserGallery(Long userId) {
        return userPersistencePort.getUserGallery(userId);
    }

    @Override
    public UserPhotoDto getUserGalleryDetails(Long photoId) {
        return userPersistencePort.getUserGalleryDetails(photoId);
    }

    @Override
    public void complaintPhoto(Long photoId) {
        userPersistencePort.complaintPhoto(photoId);
    }

    @Override
    public void deletePhotoWithComplaint(Long photoId) {
        userPersistencePort.deletePhotoWithComplaint(photoId);
    }

    @Override
    public void deleteComplaint(Long photoId) {
        userPersistencePort.deleteComplaint(photoId);
    }

    @Override
    public List<UserGalleryDto> getAllPhotoComplaint(Pageable pageable) {
        return userPersistencePort.getAllPhotoComplaint(pageable);
    }

    private boolean userWithEmailExists(String email) {
        return userPersistencePort.findUserByEmail(email).isPresent();
    }

    private boolean validateFileExtensions(MultipartFile multipartFile){
        List<String> validExtensions = Arrays.asList("jpeg", "jpg", "png");
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        return validExtensions.contains(extension);
    }
}
