package org.flywithme.acceptance

import org.flywithme.data.posts.PostCommentDto
import org.flywithme.data.posts.PostDto
import org.flywithme.entity.Comment
import org.flywithme.entity.LikePost
import org.flywithme.entity.Post
import org.flywithme.entity.User
import org.flywithme.repository.CommentRepository
import org.flywithme.repository.LikePostRepository
import org.flywithme.repository.PostsRepository
import org.flywithme.repository.UserRepository
import org.flywithme.test.AcceptanceBaseSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostsControllerTest extends AcceptanceBaseSpec{

    @Autowired
    PostsRepository postsRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CommentRepository commentRepository

    @Autowired
    LikePostRepository likePostRepository;

    def cleanup(){
        commentRepository.deleteAll()
        likePostRepository.deleteAll()
        postsRepository.deleteAll()
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


        Post post = new Post()
        post.setHeader("Fly")
        post.setUser(user)
        post.setDescription("Super fly")
        postsRepository.save(post)

        LikePost likePost = new LikePost()
        likePost.setPost(post)
        likePost.setUserId(1)
        likePostRepository.save(likePost)
    }

    def "Should get post data by id _PosTC"(){
        given:
        def id = postsRepository.findAllByUserId(userRepository.findUserByEmail("setup@gmail.com").get().getId()).get(0).getId()

        when:
        def response = restTemplate.getForEntity("$BASE_URL:$PORT/api/v1/posts/getPostData/$id",
                PostDto.class)
        def post = response.getBody()

        then:
        "Fly" == post.getHeader()
        "Super fly" == post.getDescription()

    }

    def "Should get all post by user id _PosTC"(){
        given:
        def userId = userRepository.findUserByEmail("setup@gmail.com").get().getId()

        when:
        def response = restTemplate.getForEntity("$BASE_URL:$PORT/api/v1/posts/getAllPostByUserId/$userId",
                PostDto[].class)
        def posts = response.getBody()

        then:
        1 == posts.size()
        ["Fly"] == posts*.getHeader()
        ["Super fly"] == posts*.getDescription()

    }

    def "Should delete post by id _PosTC"(){
        given:
        def id = postsRepository.findAllByUserId(userRepository.findUserByEmail("setup@gmail.com").get().getId()).get(0).getId()

        when:
        restTemplate.delete("$BASE_URL:$PORT/api/v1/posts/delete/$id")

        then:
        0 == postsRepository.findAll().size()
    }

    def "Should add like to post _PosTC"() {
        given:
        def userId = userRepository.findUserByEmail("setup@gmail.com").get().getId()
        def photoId = postsRepository.findAllByUserId(userId).get(0).getId()

        when:
        restTemplate.getForEntity("$BASE_URL:$PORT/api/v1/posts/like/$photoId/$userId", Void.class)

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
        restTemplate.exchange("$BASE_URL:$PORT/api/v1/posts/comment", HttpMethod.POST, createHttpEntity(comment), PostCommentDto.class)
        def commentAfter = commentRepository.findAll()

        then:
        commentBefore.size() == commentAfter.size() - 1
        ["TestSurname"] == commentAfter*.authorSurname.sort()
        ["Content"] == commentAfter*.comment.sort()
    }
}
