package com.degoke.ahjor.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.degoke.ahjor.entity.UserRole;
import com.degoke.ahjor.enums.UserRoleEnum;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
  Optional<UserRole> findByName(UserRoleEnum name);
}
