package org.flywithme.adapters.aws;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.flywithme.data.user.UserDto;
import org.flywithme.entity.User;
import org.flywithme.entity.UserGallery;
import org.flywithme.mappers.UserMapper;
import org.flywithme.repository.UserGalleryRepository;
import org.flywithme.repository.UserRepository;
import org.flywithme.utils.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmazonS3ImageService extends AmazonClientService {

    private final UserRepository userRepository;
    private final UserGalleryRepository userGalleryRepository;

    public UserDto uploadMainPhoto(String email, MultipartFile multipartFile){
        Optional<User> user = userRepository.findUserByEmail(email);
        removeImageFromAmazon(user.get().getMainPhotoUrl());
        user.get().setMainPhotoUrl(uploadMultipartFile(multipartFile));
        return UserMapper.INSTANCE.userToUserDto(userRepository.save(user.get()));
    }

    public UserDto uploadPhoto(String email, MultipartFile multipartFile){
        Optional<User> user = userRepository.findUserByEmail(email);
        UserGallery userGallery = UserGallery.builder().linkToPhoto(uploadMultipartFile(multipartFile)).user_id(user.get()).build();
        userGalleryRepository.save(userGallery);
        user.get().getUserGalleries().add(userGallery);
        userRepository.save(user.get());
        return UserMapper.INSTANCE.userToUserDto(user.get());
    }



    public String uploadMultipartFile(MultipartFile multipartFile) {
        String fileUrl = null;
        try {
            File file = FileUtils.convertMultipartToFile(multipartFile);
            String fileName = FileUtils.generateFileName(multipartFile);
            uploadPublicFile(fileName, file);
            file.delete();
            fileUrl = getUrl().concat(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileUrl;
    }

    public void removeImageFromAmazon(String url) {
        if (url != null) {
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            getClient().deleteObject(new DeleteObjectRequest(getBucketName(), fileName));
        }
    }


    private void uploadPublicFile(String fileName, File file) {
        getClient().putObject(new PutObjectRequest(getBucketName(), fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public boolean validateFileExtensions(MultipartFile multipartFile){
        List<String> validExtensions = Arrays.asList("jpeg", "jpg", "png", ".jpg", "JPG", ".jpeg", "JPEG", ".png", "PNG");
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        return validExtensions.contains(extension);
    }
}
