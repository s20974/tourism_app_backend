package org.flywithme.repository;

import org.flywithme.entity.LikePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikePostRepository extends JpaRepository<LikePost, Long> {

    Optional<LikePost> findByUserIdAndPostId(Long userId, Long postId);
}
