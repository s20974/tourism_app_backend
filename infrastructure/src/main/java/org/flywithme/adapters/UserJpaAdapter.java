package org.flywithme.adapters;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.bytebuddy.utility.RandomString;
import org.flywithme.adapters.aws.AmazonS3ImageService;
import org.flywithme.adapters.specifications.UserSpecifications;
import org.flywithme.data.FriendDto;
import org.flywithme.data.filters.FriendsFilter;
import org.flywithme.data.mainpage.PhotoMainPageDto;
import org.flywithme.data.mainpage.PostsMainPageDto;
import org.flywithme.data.mainpage.TripMainPageDto;
import org.flywithme.data.posts.PostCommentDto;
import org.flywithme.data.trip.JoinedUser;
import org.flywithme.data.user.*;
import org.flywithme.entity.*;
import org.flywithme.exceptions.IncorrectLoginOrPassword;
import org.flywithme.exceptions.UserNotFoundException;
import org.flywithme.mappers.UserMapper;
import org.flywithme.ports.spi.UserPersistencePort;
import org.flywithme.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class UserJpaAdapter implements UserPersistencePort {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFriendRepository userFriendRepository;

    @Autowired
    private PhotoComplaintRepository photoComplaintRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LikePhotoRepository likePhotoRepository;

    @Autowired
    private UserGalleryRepository userGalleryRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private AmazonS3ImageService amazonS3ImageService;

    Mapper mapper = DozerBeanMapperBuilder.buildDefault();

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${spring.mail.company.name}")
    private String senderName;

    @Value("${verification.url}")
    private String verifyUrl;

    @Override
    public void addUser(UserRegisterDto register) {
        var role = roleRepository.findByRoles(Roles.USER);
        User user = UserMapper.INSTANCE.userRegisterToUser(register);;
        user.setPassword(passwordEncoder.encode(register.getPassword()));
        user.setVerificationCode(RandomString.make(64));
        user.setEmailConfirmed(false);
        user.setRoles(Set.of(role.get()));
        user.setAccountNonLocked(true);
        userRepository.save(user);
        try {
            sendVerificationEmail(user);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean verifyEmail(String code) {
        User user = userRepository.findUserByVerificationCode(code).orElse(null);
        if (user!=null){
            user.setEmailConfirmed(true);
            user.setVerificationCode(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public UserDto getUserInformationByEmail(String email) {
        User user = userRepository.findUserByEmail(email).orElse(null);
        var userDtoMapped = user != null ? UserMapper.INSTANCE.userToUserDto(user) : null;
        if(userDtoMapped != null){
            userDtoMapped.setNumberOfFriends((long) user.getUserFriends().size());
            userDtoMapped.setNumberOfPosts((long) user.getPosts().size());
            return userDtoMapped;
        }
        return null;
    }

    @Override
    public UserDto uploadMainPhoto(String email, MultipartFile multipartFile) {
        return amazonS3ImageService.uploadMainPhoto(email, multipartFile);
    }

    @Override
    public UserDto uploadPhotoToGallery(String email, MultipartFile multipartFile) {
        return amazonS3ImageService.uploadPhoto(email, multipartFile);
    }

    @SneakyThrows
    @Override
    public UserDto changeUserData(String email, UserDto userDto) {
        Optional<User> userUpdated = userRepository.findUserByEmail(email);
        if(userDto.getOldPassword() != null) {
            if (validatePasswordUpdate(userUpdated.get(), userDto)) {
                userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }else {
                throw new IncorrectLoginOrPassword("Incorrect old password");
            }
        }
        User user = userUpdated.get().update(userDto);
        if(!email.equals(userDto.getEmail())) {
            String randomCode = RandomString.make(64);
            user.setVerificationCode(randomCode);
            user.setEmailConfirmed(false);
            sendVerificationEmail(user);
        }
        return UserMapper.INSTANCE.userToUserDto(userRepository.save(user));
    }

    @Override
    public UserDto addFriendToUser(Long requestFromId, Long requestToId) {
        User requestFrom = userRepository.findUserById(requestToId).get();
        User requestTo = userRepository.findUserById(requestFromId).get();


        if (userFriendRepository.getFirstByRequestFromAndRequestTo(requestFrom, requestTo).isPresent()){
            UserFriend userFriend = userFriendRepository.getFirstByRequestFromAndRequestTo(requestFrom, requestTo).get();
            if (userFriend.getRequestFrom().getEmail().equals(requestFrom.getEmail()) && userFriend.getRequestTo().getEmail().equals(requestTo.getEmail()) && !userFriend.isRequestAccepted()){
                userFriend.setRequestAccepted(true);
                userFriendRepository.save(userFriend);
                userFriend = new UserFriend();
                userFriend.setRequestAccepted(true);
                userFriend.setRequestFrom(requestTo);
                userFriend.setRequestTo(requestFrom);
                userFriendRepository.save(userFriend);
            }
        }
        return UserMapper.INSTANCE.userToUserDto(userRepository.save(requestFrom));
    }

    @Override
    public UserDto removeFriendFromUser(Long requestFromId, Long requestToId) {
        User requestFrom = userRepository.findUserById(requestFromId).get();
        User requestTo = userRepository.findUserById(requestToId).get();
        if (userFriendRepository.getFirstByRequestFromAndRequestTo(requestFrom, requestTo).isPresent()){
            UserFriend userFriend = userFriendRepository.getFirstByRequestFromAndRequestTo(requestFrom, requestTo).get();
            userFriendRepository.delete(userFriend);
        }
        if (userFriendRepository.getFirstByRequestFromAndRequestTo(requestTo, requestFrom).isPresent()){
            UserFriend userFriend = userFriendRepository.getFirstByRequestFromAndRequestTo(requestTo, requestFrom).get();
            userFriendRepository.delete(userFriend);
        }

        return UserMapper.INSTANCE.userToUserDto(requestFrom);
    }

    @Override
    public UserDto addFriendRequestToUser(Long requestFromId, Long requestToId) {
        User requestFrom = userRepository.findUserById(requestFromId).get();
        User requestTo = userRepository.findUserById(requestToId).get();
        UserFriend userFriend = new UserFriend();
        if (userFriendRepository.getFirstByRequestFromAndRequestTo(requestFrom, requestTo).isEmpty()){
            userFriend.setRequestFrom(requestFrom);
            userFriend.setRequestTo(requestTo);
            userFriendRepository.save(userFriend);
        }
        return UserMapper.INSTANCE.userToUserDto(requestTo);

    }

    @Override
    public List<FriendDto> getAllFriendsByUserId(Long id) {
        List<UserFriend> userFriends = userFriendRepository.getAllByRequestFromId(id);

        return userFriends.stream().filter(UserFriend::isRequestAccepted).map(uf -> FriendDto.builder()
                .id(uf.getRequestTo().getId())
                .name(uf.getRequestTo().getName())
                .city(uf.getRequestTo().getCity())
                .country(uf.getRequestTo().getCountry())
                .surname(uf.getRequestTo().getSurname())
                .email(uf.getRequestTo().getEmail())
                .mainPhotoUrl(uf.getRequestTo().getMainPhotoUrl())
                .build()).toList();
    }

    @Override
    public List<FriendDto> getAllFriendRequestByUserId(Long id) {
        List<UserFriend> userFriends = userFriendRepository.getAllByRequestToId(id);
        return userFriends.stream().filter(uf->!uf.isRequestAccepted()).map(uf-> FriendDto.builder()
                .id(uf.getRequestFrom().getId())
                .name(uf.getRequestFrom().getName())
                .city(uf.getRequestFrom().getCity())
                .country(uf.getRequestFrom().getCountry())
                .surname(uf.getRequestFrom().getSurname())
                .email(uf.getRequestFrom().getEmail())
                .mainPhotoUrl(uf.getRequestFrom().getMainPhotoUrl())
                .build()).collect(Collectors.toList());

    }

    @Override
    @SneakyThrows
    public FriendDto getFriendDataByFriendId(Long userId, Long friendId) {
        Optional<User> userFriend = userRepository.findUserById(friendId);

        if(userFriend.isEmpty()){
            throw new UserNotFoundException(String.format("User with id %d dont exists", friendId));
        }

        User requestFrom = userRepository.findUserById(userId).get();
        User requestTo = userRepository.findUserById(friendId).get();

        Optional<UserFriend> isSendingRequest = userFriendRepository.getFirstByRequestFromAndRequestTo(requestFrom, requestTo);

        String action = "";

        if(isSendingRequest.isPresent()){
            action = isSendingRequest.get().isRequestAccepted() ? UserFriendAction.IS_FRIEND.getResponse()
                    : UserFriendAction.IS_YOUR_REQUEST.getResponse();
        }   else {
            Optional<UserFriend> isYourRequest = userFriendRepository.getFirstByRequestFromAndRequestTo(requestTo, requestFrom);
            if(isYourRequest.isPresent()){
                if(isYourRequest.get().getRequestTo().getId() == userId) {
                    action = UserFriendAction.IS_FRIEND_REQUEST.getResponse();
                }
            } else {
                action = UserFriendAction.NOT_FRIEND.getResponse();
            }
        }

        return FriendDto.builder()
                .id(userFriend.get().getId())
                .name(userFriend.get().getName())
                .city(userFriend.get().getCity())
                .country(userFriend.get().getCountry())
                .surname(userFriend.get().getSurname())
                .email(userFriend.get().getEmail())
                .mainPhotoUrl(userFriend.get().getMainPhotoUrl())
                .userGalleries( UserMapper.INSTANCE.galleryToString(userFriend.get().getUserGalleries()))
                .action(action)
                .build();
    }

    @Override
    public Optional<String> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email).map(User::getEmail);
    }

    @Override
    public List<UserFilterDto> getUsersDataByFilter(String country, String gender, String status, Pageable pageable) {
        FriendsFilter friendsFilter = new FriendsFilter(country, gender, status);
        var users = userRepository.findAll(UserSpecifications.getUserByFilter(friendsFilter), pageable);
        return users.stream()
                .map(user -> mapper.map(user, UserFilterDto.class))
                .toList();
    }

    @Override
    public void like(Long photoId, Long userId) {
        var photo = userGalleryRepository.findById(photoId);
        var likePhoto = likePhotoRepository.findByUserIdAndUserGalleryId(userId, photoId);
        if(likePhoto.isEmpty()) {
            if (photo.isPresent()) {
                var like = LikePhoto.builder()
                        .userId(userId)
                        .userGallery(photo.get())
                        .build();
                var liked = likePhotoRepository.save(like);
                photo.get().getLikePhotos().add(liked);
                userGalleryRepository.save(photo.get());
            }
        }
    }

    @Override
    public PostCommentDto addComment(PostCommentDto postCommentDto) {
        var photo = userGalleryRepository.findById(postCommentDto.getId()).get();
        var comment = commentDtoToComment(postCommentDto, photo);
        commentRepository.save(comment);
        return postCommentDto;
    }

    @Override
    public void deletePhoto(Long id) {
        userGalleryRepository.deleteById(id);
    }

    @Override
    public List<PostsMainPageDto> getPostsMainPage(Long userId) {
        List<User> users = userRepository.findUserById(userId).get().getUserFriends()
                .stream().filter(UserFriend::isRequestAccepted)
                .map(UserFriend::getRequestTo)
                .toList();
        List<PostsMainPageDto> list = users.stream()
                .flatMap(user -> user.getPosts().stream()
                        .map(post -> userPostToDto(user, post))
                        .toList().stream()).collect(Collectors.toList());
        Collections.shuffle(list);
        return list.stream().limit(20).toList();
    }

    @Override
    public List<PhotoMainPageDto> getPhotoMainPage(Long userId) {
        List<User> users = userRepository.findUserById(userId).get().getUserFriends()
                .stream().filter(UserFriend::isRequestAccepted)
                .map(UserFriend::getRequestTo)
                .toList();
        List<PhotoMainPageDto> list = users.stream()
                .flatMap(user -> user.getUserGalleries().stream()
                        .map(photo -> userPhotoToDto(user, photo))
                        .toList().stream()).collect(Collectors.toList());
        Collections.shuffle(list);
        return list.stream().limit(20).toList();
    }

    @Override
    public List<TripMainPageDto> getTripMainPage(Long userId) {
        List<User> users = userRepository.findUserById(userId).get().getUserFriends()
                .stream().filter(UserFriend::isRequestAccepted)
                .map(UserFriend::getRequestTo)
                .toList();
        List<TripMainPageDto> list = users.stream()
                .flatMap(user -> user.getTrips().stream()
                        .map(trip -> userTripToDto(user, trip))
                        .toList().stream()).collect(Collectors.toList());
        Collections.shuffle(list);
        return list.stream().limit(20).toList();
    }

    @Override
    public List<UserGalleryDto> getUserGallery(Long userId) {
        var gallery = userRepository.findUserById(userId).get().getUserGalleries();
        return gallery.stream().map(gallery1 -> UserGalleryDto.builder()
                .comments((long) gallery1.getComments().size())
                .id(gallery1.getId())
                .likes((long) gallery1.getLikePhotos().size())
                .url(gallery1.getLinkToPhoto())
                .build()).collect(Collectors.toList());
    }

    @Override
    public UserPhotoDto getUserGalleryDetails(Long photoId) {
        var photo = userGalleryRepository.findById(photoId).get();
        var comments = commentToDto(photo.getComments());
        return UserPhotoDto.builder()
                .commentDtos(comments)
                .id(photoId)
                .likes((long) photo.getLikePhotos().size())
                .build();
    }

    @Override
    public void complaintPhoto(Long photoId) {
        var photo = userGalleryRepository.findById(photoId);
        var complaintExists = photoComplaintRepository.findByUserGalleryId(photoId);
        if(photo.isPresent() && complaintExists.isEmpty()){
            var complaint = new PhotoComplaint();
            complaint.setUserGallery(photo.get());
            photoComplaintRepository.save(complaint);
        }
    }

    @Override
    public void deletePhotoWithComplaint(Long photoId) {
        var complaint = photoComplaintRepository.findByUserGalleryId(photoId);
        if(complaint.isPresent()){
            userGalleryRepository.deleteById(photoId);
        }
    }
    @Transactional
    @Override
    public void deleteComplaint(Long photoId) {
        photoComplaintRepository.deleteByUserGalleryId(photoId);
    }

    @Override
    public List<UserGalleryDto> getAllPhotoComplaint(Pageable pageable) {
        var photo = photoComplaintRepository.findAll(pageable);
        return photo.stream().map(complaint -> userGalleryDto(complaint.getUserGallery())).toList();
    }

    private List<PostCommentDto> commentToDto(Set<Comment> comments){
        return comments.stream().map(comment -> PostCommentDto.builder()
                .authorName(comment.getAuthorName())
                .authorSurname(comment.getAuthorSurname())
                .content(comment.getComment())
                .build()).collect(Collectors.toList());
    }

    private UserGalleryDto userGalleryDto(UserGallery userGallery){
        return UserGalleryDto.builder()
                .id(userGallery.getId())
                .url(userGallery.getLinkToPhoto())
                .likes((long) userGallery.getLikePhotos().size())
                .comments((long) userGallery.getComments().size())
                .build();
    }

    private PostsMainPageDto userPostToDto(User user, Post post){
        return PostsMainPageDto.builder()
                .postId(post.getId())
                .userId(user.getId())
                .postPhotoUrl(post.getPhotoUrl())
                .userPhotoUrl(user.getMainPhotoUrl())
                .description(post.getDescription())
                .header(post.getHeader())
                .geoLocation(post.getGeoLocation())
                .build();
    }

    private PhotoMainPageDto userPhotoToDto(User user, UserGallery userGallery){
        return PhotoMainPageDto.builder()
                .photoId(userGallery.getId())
                .photoUrl(userGallery.getLinkToPhoto())
                .userId(user.getId())
                .userMainPhoto(user.getMainPhotoUrl())
                .build();
    }

    private TripMainPageDto userTripToDto(User user, Trip trip){
        return TripMainPageDto.builder()
                .tripId(trip.getId())
                .userId(user.getId())
                .userMainPhoto(user.getMainPhotoUrl())
                .dateFrom(trip.getDateFrom())
                .dateTo(trip.getDateTo())
                .country(trip.getCountry())
                .maxPeople(trip.getMaxPeople())
                .description(trip.getDescription())
                .header(trip.getHeader())
                .joinedUsers(getList(trip))
                .build();
    }

    private Set<JoinedUser> getList(Trip trip){
        Set<JoinedUser> set = new HashSet<>();
        set.add(new JoinedUser(trip.getUser().getId(), trip.getUser().getMainPhotoUrl()));
        trip.getListOfJoinedUsers().forEach(user -> {
            set.add(new JoinedUser(user.getId(), user.getMainPhotoUrl()));
        });
        return set;
    }

    public void sendVerificationEmail(User user)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href='http://[[URL]]' target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "FlyWithMe";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getName() + " " + user.getSurname());
        String verifyURL = verifyUrl + user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);
        mailSender.send(message);
    }

    private boolean validatePasswordUpdate(User user, UserDto userDto){
        return passwordEncoder.matches(userDto.getOldPassword(), user.getPassword());
    }

    public UserJpaAdapter(UserRepository userRepository, UserFriendRepository userFriendRepository, PasswordEncoder passwordEncoder, JavaMailSender mailSender, AmazonS3ImageService amazonS3ImageService, String fromAddress, String senderName, String verifyUrl, UserGalleryRepository userGalleryRepository, PhotoComplaintRepository photoComplaintRepository) {
        this.userRepository = userRepository;
        this.userFriendRepository = userFriendRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.amazonS3ImageService = amazonS3ImageService;
        this.fromAddress = fromAddress;
        this.senderName = senderName;
        this.verifyUrl = verifyUrl;
        this.userGalleryRepository = userGalleryRepository;
        this.photoComplaintRepository = photoComplaintRepository;
    }

    private Comment commentDtoToComment(PostCommentDto postCommentDto, UserGallery userGallery){
        return Comment.builder()
                .userGallery(userGallery)
                .comment(postCommentDto.getContent())
                .authorName(postCommentDto.getAuthorName())
                .authorSurname(postCommentDto.getAuthorSurname())
                .build();
    }

}
