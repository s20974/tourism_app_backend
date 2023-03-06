package org.flywithme.acceptance

import org.flywithme.entity.LikePost
import org.flywithme.entity.PhotoComplaint
import org.flywithme.entity.Post
import org.flywithme.entity.PostComplaint
import org.flywithme.entity.User
import org.flywithme.entity.UserGallery
import org.flywithme.repository.CommentRepository
import org.flywithme.repository.LikePostRepository
import org.flywithme.repository.PhotoComplaintRepository
import org.flywithme.repository.PostComplaintRepository
import org.flywithme.repository.PostsRepository
import org.flywithme.repository.UserGalleryRepository
import org.flywithme.repository.UserRepository
import org.flywithme.test.AcceptanceBaseSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ComplaintControllerTest extends AcceptanceBaseSpec{

    @Autowired
    PostsRepository postsRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CommentRepository commentRepository

    @Autowired
    LikePostRepository likePostRepository

    @Autowired
    PostComplaintRepository postComplaintRepository

    @Autowired
    UserGalleryRepository userGalleryRepository

    @Autowired
    PhotoComplaintRepository photoComplaintRepository

    def cleanup(){
        commentRepository.deleteAll()
        likePostRepository.deleteAll()
        postComplaintRepository.deleteAll()
        postsRepository.deleteAll()
        photoComplaintRepository.deleteAll()
        userGalleryRepository.deleteAll()
        userRepository.deleteAll()
    }

    def setup(){

        User user = User.builder()
                .name("TestUser")
                .surname("Test")
                .password("1111E-!")
                .email("setup@gmail.com")
                .emailConfirmed(true)
                .build()
        userRepository.save(user)

        def photo = UserGallery.builder()
                .user_id(user)
                .build()
        userGalleryRepository.save(photo)

        def photoComplaint = new PhotoComplaint()
        photoComplaint.setUserGallery(photo)
        photoComplaintRepository.save(photoComplaint)

        Post post = new Post()
        post.setHeader("Fly")
        post.setUser(user)
        post.setDescription("Super fly")
        postsRepository.save(post)

        LikePost likePost = LikePost.builder()
                .post(post)
                .userId(1)
                .build()
        likePostRepository.save(likePost)

        def postComplaint = new PostComplaint()
        postComplaint.setPost(post)
        postComplaintRepository.save(postComplaint)
    }

    def "Should add complaint to post by id _PosTC"(){
        given:
        def id = postsRepository.findAll().first().getId()

        when:
        restTemplate.getForEntity("$BASE_URL:$PORT/api/v1/complaint/post?postId=$id",
                Void.class)

        then:
        postComplaintRepository.findByPostId(id).isPresent()
    }

    def "Should add complaint photo by id _PosTc"(){
        given:
        def id = userGalleryRepository.findAll().first().getId()

        when:
        restTemplate.getForEntity("$BASE_URL:$PORT/api/v1/complaint/photo?photoId=$id",
                Void.class)

        then:
        1 == photoComplaintRepository.findAll().size()
    }

    def "Should delete post by id _PosTc"(){
        given:
        def id = postsRepository.findAll().first().getId()

        when:
        restTemplate.delete("$BASE_URL:$PORT/api/v1/complaint/deletePost?postId=$id")

        then:
        0 == postsRepository.findAll().size()
    }

    def "Should delete complaint post by id _PosTc"(){
        given:
        def id = postsRepository.findAll().first().getId()

        when:
        restTemplate.delete("$BASE_URL:$PORT/api/v1/complaint/deleteComplaintPost?postId=$id")

        then:
        0 == postComplaintRepository.findAll().size()
    }

    def "Should delete photo by id _PosTc"(){
        given:
        def id = userGalleryRepository.findAll().first().getId()

        when:
        restTemplate.delete("$BASE_URL:$PORT/api/v1/complaint/deletePhoto?photoId=$id")

        then:
        0 == userGalleryRepository.findAll().size()
    }

    def "Should delete complain photo by id _PosTc"(){
        def id = userGalleryRepository.findAll().first().getId()

        when:
        restTemplate.delete("$BASE_URL:$PORT/api/v1/complaint/deleteComplaintPhoto?photoId=$id")

        then:
        0 == photoComplaintRepository.findAll().size()
    }
}
