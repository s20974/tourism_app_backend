package org.flywithme.mappers;

import org.flywithme.data.user.UserDto;
import org.flywithme.data.user.UserRegisterDto;
import org.flywithme.entity.User;
import org.flywithme.entity.UserGallery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto userToUserDto(User user);

    User userDtoToUser(UserDto userDto);

    User userRegisterToUser(UserRegisterDto userRegisterDto);

    List<String> galleryToString(List<UserGallery> userGalleries);

    default String map(UserGallery userGallery){
        return userGallery.getLinkToPhoto();
    }

    List<UserGallery> stringToGallery(List<String> photos);

    default UserGallery map(String string){
        return UserGallery.builder().linkToPhoto(string).build();
    }
}
