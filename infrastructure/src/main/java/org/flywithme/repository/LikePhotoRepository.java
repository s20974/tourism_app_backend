package org.flywithme.repository;

import org.flywithme.entity.LikePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikePhotoRepository extends JpaRepository<LikePhoto, Long> {

    Optional<LikePhoto> findByUserIdAndUserGalleryId(Long userId, Long userGalleryId);
}
