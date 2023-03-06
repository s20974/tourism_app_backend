package org.flywithme.entity;

import lombok.*;

import javax.persistence.*;

@Entity(name = "comment_tb")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;

    private String authorName;

    private String authorSurname;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "postId")
    private Post post;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "photoId")
    private UserGallery userGallery;
}
