package org.flywithme.repository;

import org.flywithme.entity.Role;
import org.flywithme.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoles(Roles roles);
}
