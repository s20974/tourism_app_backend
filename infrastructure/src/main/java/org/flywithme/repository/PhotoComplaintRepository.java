package org.flywithme.repository;

import org.flywithme.entity.PhotoComplaint;
import org.flywithme.entity.PostComplaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhotoComplaintRepository extends JpaRepository<PhotoComplaint, Long> {

    Optional<PhotoComplaint> findByUserGalleryId(Long userGalleryId);

    void deleteByUserGalleryId(Long userGalleryId);

}
