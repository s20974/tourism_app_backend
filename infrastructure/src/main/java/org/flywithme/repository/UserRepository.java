package org.flywithme.repository;

import org.flywithme.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByVerificationCode(String verificationCode);

    Optional<User> findUserById (Long id);

    List<User> findAll(Specification<User> specification, Pageable pageable);
}
