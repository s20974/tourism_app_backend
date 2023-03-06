package org.flywithme.repository;

import org.flywithme.entity.PostComplaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostComplaintRepository extends JpaRepository<PostComplaint, Long> {

    Optional<PostComplaint> findByPostId(Long postId);

    void deleteByPostId(Long postId);
}
