package org.flywithme.repository;

import org.flywithme.entity.UserGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGalleryRepository extends JpaRepository<UserGallery, Long> {
}
