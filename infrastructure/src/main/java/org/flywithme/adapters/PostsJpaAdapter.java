package org.flywithme.adapters;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import lombok.NoArgsConstructor;
import org.flywithme.adapters.aws.AmazonS3ImageService;
import org.flywithme.data.posts.NewPostDto;
import org.flywithme.data.posts.PostCommentDto;
import org.flywithme.data.posts.PostDto;
import org.flywithme.entity.*;
import org.flywithme.mappers.PostMapper;
import org.flywithme.ports.spi.PostsPersistencePort;
import org.flywithme.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@NoArgsConstructor
public class PostsJpaAdapter implements PostsPersistencePort {

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private PostComplaintRepository postComplaintRepository;

    @Autowired
    private LikePostRepository likePostRepository;

    Mapper mapper = DozerBeanMapperBuilder.buildDefault();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AmazonS3ImageService amazonS3ImageService;

    @Override
    public boolean newPost(NewPostDto newPostDto, MultipartFile multipartFile) {
        String url = uploadFiles(multipartFile);
        newPostDto.setPhotoUrl(url);
        User user = userRepository.findUserByEmail(newPostDto.getUserEmail()).get();
        Post post = PostMapper.INSTANCE.newPostDtoToPost(newPostDto);
        post.setUser(user);
        postsRepository.save(post);
        return true;
    }

    @Override
    public void deletePost(Long id) {
        postsRepository.deleteById(id);
    }

    @Override
    public PostDto getPost(Long id) {
        Post post = postsRepository.getById(id);
        return postToDto(post);
    }

    @Override
    public List<PostDto> getAllPostsByUserId(Long id) {
        List<Post> posts = postsRepository.findAllByUserId(id);
        return posts.stream()
                .map(this::postToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void like(Long postId, Long userId) {
        var post = postsRepository.findById(postId);
        var likePost = likePostRepository.findByUserIdAndPostId(userId, postId);
        if(likePost.isEmpty()) {
            if (post.isPresent()) {
                var like = LikePost.builder()
                        .userId(userId)
                        .post(post.get())
                        .build();
                var liked = likePostRepository.save(like);
                post.get().getLikePosts().add(liked);
                postsRepository.save(post.get());
            }
        }
    }

    @Override
    public PostCommentDto addComment(PostCommentDto postCommentDto) {
        var post = postsRepository.findById(postCommentDto.getId()).get();
        var comment = commentDtoToComment(postCommentDto, post);
        commentRepository.save(comment);
        return postCommentDto;
    }

    @Override
    public void complaintPost(Long postId) {
        var post = postsRepository.findById(postId);
        var complaintExists = postComplaintRepository.findByPostId(postId);
        if(post.isPresent() && complaintExists.isEmpty()){
            var complaint = new PostComplaint();
            complaint.setPost(post.get());
            postComplaintRepository.save(complaint);
        }
    }

    @Override
    public void deletePostWithComplaint(Long postId) {
        var complaint = postComplaintRepository.findByPostId(postId);
        if(complaint.isPresent()){
            postsRepository.deleteById(postId);
        }
    }

    @Override
    public void deleteComplaint(Long postId) {
        postComplaintRepository.deleteByPostId(postId);
    }

    @Override
    public List<PostDto> getAllPostComplaint(Pageable pageable) {
        var posts = postComplaintRepository.findAll(pageable);
        return posts.stream().map(complaint -> postToDto(complaint.getPost())).toList();
    }

    private String uploadFiles(MultipartFile multipartFile){
        if(amazonS3ImageService.validateFileExtensions(multipartFile)){
            return amazonS3ImageService.uploadMultipartFile(multipartFile);
        }
        return null;
    }

    public PostsJpaAdapter(PostsRepository postsRepository, UserRepository userRepository, AmazonS3ImageService amazonS3ImageService, PostComplaintRepository postComplaintRepository) {
        this.postsRepository = postsRepository;
        this.userRepository = userRepository;
        this.amazonS3ImageService = amazonS3ImageService;
        this.postComplaintRepository = postComplaintRepository;
    }

    private Comment commentDtoToComment(PostCommentDto postCommentDto, Post post){
        return Comment.builder()
                .post(post)
                .comment(postCommentDto.getContent())
                .authorName(postCommentDto.getAuthorName())
                .authorSurname(postCommentDto.getAuthorSurname())
                .build();
    }

    private List<PostCommentDto> commentToDto(Set<Comment> comments){
        return comments.stream().map(comment -> PostCommentDto.builder()
                .authorName(comment.getAuthorName())
                .authorSurname(comment.getAuthorSurname())
                .content(comment.getComment())
                .build()).collect(Collectors.toList());
    }

    private PostDto postToDto(Post post){
        return PostDto.builder()
                .id(post.getId())
                .description(post.getDescription())
                .geoLocation(post.getGeoLocation())
                .likes((long) post.getLikePosts().size())
                .comment(commentToDto(post.getComments()))
                .photoUrl(post.getPhotoUrl())
                .header(post.getHeader())
                .build();
    }
}
