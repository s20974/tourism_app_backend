package org.flywithme.mappers;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.flywithme.data.enums.Gender;
import org.flywithme.data.enums.Status;
import org.flywithme.data.user.UserDto;
import org.flywithme.data.user.UserDto.UserDtoBuilder;
import org.flywithme.data.user.UserRegisterDto;
import org.flywithme.entity.User;
import org.flywithme.entity.User.UserBuilder;
import org.flywithme.entity.UserGallery;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-03-06T21:42:53+0100",
    comments = "version: 1.4.2.Final, compiler: Eclipse JDT (IDE) 3.33.0.v20230213-1046, environment: Java 17.0.6 (Eclipse Adoptium)"
)
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto userToUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDtoBuilder userDto = UserDto.builder();

        userDto.city( user.getCity() );
        userDto.country( user.getCountry() );
        userDto.email( user.getEmail() );
        if ( user.getGender() != null ) {
            userDto.gender( user.getGender().name() );
        }
        userDto.id( user.getId() );
        userDto.mainPhotoUrl( user.getMainPhotoUrl() );
        userDto.name( user.getName() );
        userDto.password( user.getPassword() );
        userDto.phoneNumber( user.getPhoneNumber() );
        if ( user.getStatus() != null ) {
            userDto.status( user.getStatus().name() );
        }
        userDto.surname( user.getSurname() );
        userDto.userGalleries( galleryToString( user.getUserGalleries() ) );

        return userDto.build();
    }

    @Override
    public User userDtoToUser(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        UserBuilder user = User.builder();

        user.city( userDto.getCity() );
        user.country( userDto.getCountry() );
        user.email( userDto.getEmail() );
        if ( userDto.getGender() != null ) {
            user.gender( Enum.valueOf( Gender.class, userDto.getGender() ) );
        }
        user.id( userDto.getId() );
        user.mainPhotoUrl( userDto.getMainPhotoUrl() );
        user.name( userDto.getName() );
        user.password( userDto.getPassword() );
        user.phoneNumber( userDto.getPhoneNumber() );
        if ( userDto.getStatus() != null ) {
            user.status( Enum.valueOf( Status.class, userDto.getStatus() ) );
        }
        user.surname( userDto.getSurname() );
        user.userGalleries( stringToGallery( userDto.getUserGalleries() ) );

        return user.build();
    }

    @Override
    public User userRegisterToUser(UserRegisterDto userRegisterDto) {
        if ( userRegisterDto == null ) {
            return null;
        }

        UserBuilder user = User.builder();

        user.email( userRegisterDto.getEmail() );
        user.name( userRegisterDto.getName() );
        user.password( userRegisterDto.getPassword() );
        user.surname( userRegisterDto.getSurname() );

        return user.build();
    }

    @Override
    public List<String> galleryToString(List<UserGallery> userGalleries) {
        if ( userGalleries == null ) {
            return null;
        }

        List<String> list = new ArrayList<String>( userGalleries.size() );
        for ( UserGallery userGallery : userGalleries ) {
            list.add( map( userGallery ) );
        }

        return list;
    }

    @Override
    public List<UserGallery> stringToGallery(List<String> photos) {
        if ( photos == null ) {
            return null;
        }

        List<UserGallery> list = new ArrayList<UserGallery>( photos.size() );
        for ( String string : photos ) {
            list.add( map( string ) );
        }

        return list;
    }
}
