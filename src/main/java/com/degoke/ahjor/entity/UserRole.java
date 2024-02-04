package com.degoke.ahjor.entity;

import java.util.UUID;

import com.degoke.ahjor.enums.UserRoleEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity()
@Table(
  name = "user_roles"
)
@Getter
@Setter
public class UserRole {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Enumerated(EnumType.STRING)
  private UserRoleEnum name;

  public UserRole() {}

  public UserRole(UserRoleEnum name) {
    this.name = name;
  }
}
