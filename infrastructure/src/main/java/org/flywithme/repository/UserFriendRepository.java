package org.flywithme.repository;

import org.flywithme.entity.User;
import org.flywithme.entity.UserFriend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserFriendRepository extends JpaRepository<UserFriend, Long> {

    Optional<UserFriend> getFirstByRequestFromAndRequestTo(User requestFrom, User requestTo);

    Optional<UserFriend> getFirstByRequestToAndRequestFrom(User requestTo, User requestFrom);

    List<UserFriend> getAllByRequestFromId(Long requestFromId);

    List<UserFriend> getAllByRequestToId(Long requestFromId);

}
