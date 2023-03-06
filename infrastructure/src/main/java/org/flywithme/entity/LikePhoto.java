package org.flywithme.entity;

import lombok.*;

import javax.persistence.*;

@Entity(name = "like_photo_tb")
@Getter
@Setter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class LikePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @EqualsAndHashCode.Include
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "photoId")
    private UserGallery userGallery;
}
