package com.healthcare.portal.repository;

import com.healthcare.portal.entity.Role;
import com.healthcare.portal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    long countByRole(Role role);
    long countByRoleAndActive(Role role, boolean active);
}
