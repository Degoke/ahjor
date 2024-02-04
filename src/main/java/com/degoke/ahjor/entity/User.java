package com.degoke.ahjor.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.degoke.ahjor.enums.UserStatusEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

@Entity()
@Table(name = "users",
  uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
  }
)
@Getter
@Setter
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column
  private String firstName;

  @Column
  private String lastName;

  @Column
  private String email;
  
  @Column
  private String password;

  @Enumerated(EnumType.STRING)
  private UserStatusEnum status;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
    name = "user_role_join",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "user_role_id")
  )
  private Set<UserRole> roles = new HashSet<>();

  @CreationTimestamp
  private LocalDateTime createdTimestamp;

  @UpdateTimestamp
  private LocalDateTime updatedTimestamp;

  public User() {}

  public User(String firstName, String lastName, String email, String password) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.password = password;
  }
}
