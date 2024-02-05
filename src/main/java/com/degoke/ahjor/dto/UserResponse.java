package com.degoke.ahjor.dto;

import java.util.List;
import java.util.UUID;

import com.degoke.ahjor.enums.UserStatusEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private UserStatusEnum status;

    public UserResponse(UUID id, String email, String firstName, String lastName, UserStatusEnum status) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
    }
}
