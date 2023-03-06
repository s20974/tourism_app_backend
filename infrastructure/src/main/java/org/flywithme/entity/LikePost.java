package org.flywithme.entity;

import lombok.*;

import javax.persistence.*;

@Entity(name = "like_post_tb")
@Getter
@Setter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class LikePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @EqualsAndHashCode.Include
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;
}
