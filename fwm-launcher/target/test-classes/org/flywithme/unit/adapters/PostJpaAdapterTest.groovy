package org.flywithme.unit.adapters

import org.flywithme.adapters.PostsJpaAdapter
import org.flywithme.adapters.aws.AmazonS3ImageService
import org.flywithme.data.enums.Gender
import org.flywithme.data.enums.Status
import org.flywithme.data.posts.NewPostDto
import org.flywithme.entity.LikePost
import org.flywithme.entity.Post
import org.flywithme.entity.PostComplaint
import org.flywithme.entity.User
import org.flywithme.repository.LikePostRepository
import org.flywithme.repository.PostComplaintRepository
import org.flywithme.repository.PostsRepository
import org.flywithme.repository.UserRepository
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification

class PostJpaAdapterTest extends Specification{

    PostsRepository postsRepository = Mock()
    UserRepository userRepository = Mock()
    AmazonS3ImageService amazonS3ImageService = Mock()
    LikePostRepository likePostRepository = Mock()
    MultipartFile multipartFile = Mock()
    PostComplaintRepository postComplaintRepository = Mock()

    PostsJpaAdapter postsJpaAdapter = new PostsJpaAdapter(postsRepository: postsRepository, userRepository: userRepository, amazonS3ImageService: amazonS3ImageService, likePostRepository:likePostRepository, postComplaintRepository: postComplaintRepository)

    def "Should add new post Pos_Tc"(){
        given:
        def alfaUser = User.builder()
                .id(1)
                .name("AlfaUser")
                .surname("Test")
                .password("1111E-!")
                .email("alfa@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        def newPostDto = NewPostDto.builder()
        .userEmail("alfa@gmail.com")
        .header("Test")
        .description("Test")
        .build()

        1 * userRepository.findUserByEmail("alfa@gmail.com") >> Optional.of(alfaUser)

        when :
        def result = postsJpaAdapter.newPost(newPostDto, multipartFile)

        then:
        result
        1 * postsRepository.save(_ as Post)
    }

    def "Should add like to post _PosTC"(){
        given:
        def alfaUser = User.builder()
                .id(1)
                .name("AlfaUser")
                .surname("Test")
                .password("1111E-!")
                .email("alfa@gmail.com")
                .emailConfirmed(true)
                .status(Status.FOUND)
                .gender(Gender.MALE)
                .country("Poland")
                .build()

        def post = new Post()
        post.setId(1)
        post.setDescription("Test")
        post.setHeader("Test")
        post.setUser(alfaUser)

        def like = LikePost.builder()
                .userId(alfaUser.getId())
                .post(post)
                .build()

        def likes = new HashSet<LikePost>()
        likes.add(like)
        post.setLikePosts(likes)

        1 * postsRepository.findById(post.getId()) >> Optional.of(post)
        1 * likePostRepository.save(like) >> like

        when :
        postsJpaAdapter.like(post.getId(), alfaUser.getId())

        then:
        1 * postsRepository.save(post)

    }

    def "Should add complaint to post by id _PosTC"(){
        def post = new Post()
        post.setId(1)
        post.setDescription("Test")
        post.setHeader("Test")

        def postComplaint = new PostComplaint()
        postComplaint.setPost(post)
        postComplaintRepository.save(postComplaint)

        postsRepository.findById(post.getId()) >> Optional.of(post)
        postComplaintRepository.findByPostId(post.getId()) >> Optional.empty()

        when:
        postsJpaAdapter.complaintPost(post.getId())

        then:
        1 * postComplaintRepository.save(_ as PostComplaint)
    }

    def "Should not add complaint to post by id _PosTC"(){
        def post = new Post()
        post.setId(1)
        post.setDescription("Test")
        post.setHeader("Test")

        def postComplaint = new PostComplaint()
        postComplaint.setPost(post)
        postComplaintRepository.save(postComplaint)

        postsRepository.findById(post.getId()) >> Optional.of(post)
        postComplaintRepository.findByPostId(post.getId()) >> Optional.of(postComplaint)

        when:
        postsJpaAdapter.complaintPost(post.getId())

        then:
        0 * postComplaintRepository.save(_ as PostComplaint)
    }

    def "Should delete post with complaint by post id _PosTC"(){
        def post = new Post()
        post.setId(1)
        post.setDescription("Test")
        post.setHeader("Test")

        def postComplaint = new PostComplaint()
        postComplaint.setPost(post)
        postComplaintRepository.save(postComplaint)

        postComplaintRepository.findByPostId(post.getId()) >> Optional.of(postComplaint)

        when:
        postsJpaAdapter.deletePostWithComplaint(post.getId())

        then:
        1 * postsRepository.deleteById(post.getId())
    }

    def "Should not delete post with complaint by post id _PosTC"(){
        def post = new Post()
        post.setId(1)
        post.setDescription("Test")
        post.setHeader("Test")

        def postComplaint = new PostComplaint()
        postComplaint.setPost(post)
        postComplaintRepository.save(postComplaint)

        postComplaintRepository.findByPostId(post.getId()) >> Optional.empty()

        when:
        postsJpaAdapter.deletePostWithComplaint(post.getId())

        then:
        0 * postsRepository.deleteById(post.getId())
    }
}
