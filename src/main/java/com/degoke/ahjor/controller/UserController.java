package com.degoke.ahjor.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.degoke.ahjor.dto.UserResponse;
import com.degoke.ahjor.entity.User;
import com.degoke.ahjor.exception.ResourceNotFoundException;
import com.degoke.ahjor.repository.UserRepository;
import com.degoke.ahjor.security.service.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User", description = "User Api's")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {
  @Autowired
  UserRepository userRepository;

    @Operation(summary = "Get current User")
    @ApiResponses({
      @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = UserResponse.class)),
      }),
    @ApiResponse(responseCode = "400", content = {
        @Content(schema = @Schema(implementation = ResourceNotFoundException.class)),
      }),
    })
  @GetMapping("/me")
  public ResponseEntity<?> currentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    UUID id = userDetails.getId();

    User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("user not found"));

    return ResponseEntity.ok(new UserResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getStatus()));
  }
}
