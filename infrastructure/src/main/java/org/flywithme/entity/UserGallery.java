package org.flywithme.entity;


import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "user_gallery_tb")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserGallery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String linkToPhoto;

    @OneToMany(mappedBy = "userGallery", cascade = CascadeType.ALL)
    private Set<LikePhoto> likePhotos;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user_id;

    @OneToMany(mappedBy = "userGallery", cascade = CascadeType.ALL)
    private Set<Comment> comments;

    @OneToMany(mappedBy = "userGallery", cascade = CascadeType.REMOVE)
    private Set<PhotoComplaint> photoComplaints;
}
