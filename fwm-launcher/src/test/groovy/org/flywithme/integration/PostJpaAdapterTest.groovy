package org.flywithme.integration

import org.flywithme.adapters.PostsJpaAdapter
import org.flywithme.data.posts.PostCommentDto
import org.flywithme.entity.LikePost
import org.flywithme.entity.Post
import org.flywithme.entity.PostComplaint
import org.flywithme.entity.User
import org.flywithme.entity.UserGallery
import org.flywithme.repository.CommentRepository
import org.flywithme.repository.LikePostRepository
import org.flywithme.repository.PostComplaintRepository
import org.flywithme.repository.PostsRepository
import org.flywithme.repository.UserGalleryRepository
import org.flywithme.repository.UserRepository
import org.flywithme.test.AcceptanceBaseSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostJpaAdapterTest extends AcceptanceBaseSpec{

    @Autowired
    PostsJpaAdapter postsJpaAdapter

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



    def cleanup(){
        commentRepository.deleteAll()
        likePostRepository.deleteAll()
        postComplaintRepository.deleteAll()
        postsRepository.deleteAll()
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

    def "Should get post data by id _PosTC"(){
        given:
        def id = postsRepository.findAllByUserId(userRepository.findUserByEmail("setup@gmail.com").get().getId()).get(0).getId()

        when:
        def post = postsJpaAdapter.getPost(id)

        then:
        "Fly" == post.getHeader()
        "Super fly" == post.getDescription()

    }

    def "Should get all post by user id _PosTC"(){
        given:
        def userId = userRepository.findUserByEmail("setup@gmail.com").get().getId()

        when:
        def posts = postsJpaAdapter.getAllPostsByUserId(userId)

        then:
        1 == posts.size()
        ["Fly"] == posts*.getHeader()
        ["Super fly"] == posts*.getDescription()

    }

    def "Should delete post by id _PosTC"(){
        given:
        def id = postsRepository.findAllByUserId(userRepository.findUserByEmail("setup@gmail.com").get().getId()).get(0).getId()

        when:
        postsJpaAdapter.deletePost(id)

        then:
        0 == postsRepository.findAll().size()
    }

    def "Should add like to post _PosTC"() {
        given:
        def userId = userRepository.findUserByEmail("setup@gmail.com").get().getId()
        def photoId = postsRepository.findAllByUserId(userId).get(0).getId()

        when:
        postsJpaAdapter.like(photoId, userId)

        then:
        2 == postsRepository.findAllByUserId(userId).get(0).getLikePosts().size()
    }

    def "Should add comment to post _PosTC"() {
        given:
        def postId = postsRepository.findAllByUserId(userRepository.findUserByEmail("setup@gmail.com").get().getId()).get(0).getId()
        def comment = PostCommentDto.builder()
                .authorName("TestName")
                .authorSurname("TestSurname")
                .content("Content")
                .id(postId)
                .build()
        def commentBefore = commentRepository.findAll()

        when:
        postsJpaAdapter.addComment(comment)
        def commentAfter = commentRepository.findAll()

        then:
        commentBefore.size() == commentAfter.size() - 1
        ["TestSurname"] == commentAfter*.authorSurname.sort()
        ["Content"] == commentAfter*.comment.sort()
    }

    def "Should add complain to post by id _PosTC"(){
        given:
        def id = postsRepository.findAll().first().getId()

        when:
        postsJpaAdapter.complaintPost(id)

        then:
        1 == postComplaintRepository.findAll().size()
    }

    def "Should delete post with complain _PosTC"(){
        given:
        def id = postsRepository.findAll().first().getId()

        when:
        postsJpaAdapter.deletePostWithComplaint(id)

        then:
        0 == postsRepository.findAll().size()
    }

    def "Should delete complain by post id Pos_Tc"(){
        given:
        def id = postsRepository.findAll().first().getId()

        when:
        postsJpaAdapter.deleteComplaint(id)

        then:
        0 == postComplaintRepository.findAll().size()
    }

}
