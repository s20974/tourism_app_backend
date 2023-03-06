package org.flywithme.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity(name = "post_tb")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String header;

    private String photoUrl;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<LikePost> likePosts;

    private String description;

    private String geoLocation;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<Comment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private Set<PostComplaint> complaints;
}
