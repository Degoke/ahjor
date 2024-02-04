package com.degoke.ahjor.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.degoke.ahjor.dto.JwtResponse;
import com.degoke.ahjor.dto.LoginRequest;
import com.degoke.ahjor.dto.MessageResponse;
import com.degoke.ahjor.dto.RegisterRequest;
import com.degoke.ahjor.dto.VerifyEmailRequest;
import com.degoke.ahjor.dto.EmailDetails;
import com.degoke.ahjor.entity.User;
import com.degoke.ahjor.entity.UserOtp;
import com.degoke.ahjor.entity.UserRole;
import com.degoke.ahjor.enums.UserRoleEnum;
import com.degoke.ahjor.enums.UserStatusEnum;
import com.degoke.ahjor.exception.ResourceNotFoundException;
import com.degoke.ahjor.security.jwt.JwtUtils;
import com.degoke.ahjor.security.service.UserDetailsImpl;
import com.degoke.ahjor.service.EmailService;
import com.degoke.ahjor.util.Generator;
import com.degoke.ahjor.util.TimeUtils;
import com.degoke.ahjor.repository.UserOtpRepository;
import com.degoke.ahjor.repository.UserRepository;
import com.degoke.ahjor.repository.UserRoleRepository;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth", description = "Authentication Api's")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRoleRepository  userRoleRepository;

    @Autowired
    UserOtpRepository userOtpRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Operation(summary = "Create a new user")
    @ApiResponses({
      @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = JwtResponse.class)),
      }),
      @ApiResponse(responseCode = "400", content = {
        @Content(schema = @Schema(implementation = MessageResponse.class)),
      }),
    })
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Email is already in use"));
        }

        User user = new User(registerRequest.getFirstName(),
            registerRequest.getLastName(),
            registerRequest.getEmail(),
            encoder.encode(registerRequest.getPassword()));

        Set<String> strRoles = registerRequest.getRole();
        Set<UserRole> roles = new HashSet<>();

        if (strRoles == null) {
            UserRole userRole = userRoleRepository.findByName(UserRoleEnum.USER)
                .orElseThrow(() -> new ResourceNotFoundException("role not found"));
            
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        UserRole adminRole = userRoleRepository.findByName(UserRoleEnum.ADMIN)
                            .orElseThrow(() -> new ResourceNotFoundException("role not found"));
                        roles.add(adminRole);
                        break;
                    default:
                        UserRole userRole = userRoleRepository.findByName(UserRoleEnum.USER)
                            .orElseThrow(() -> new ResourceNotFoundException("role not found"));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        char[] code = Generator.generateOtp(6);

        String stringOtp = new String(code);
        Date expiry = TimeUtils.addMinutes(10);

        UserOtp otp = userOtpRepository.findByUserId(user.getId())
            .orElse(new UserOtp(stringOtp, expiry));

        if (otp.getUser() != null) {
            otp.setOtp(stringOtp);
            otp.setExpiry(expiry);
        } else {
            otp.setUser(user);
        }

        userOtpRepository.save(otp);

        EmailDetails emailDetails = new EmailDetails(
            user.getEmail(),
            "Moni wallet Email verification",
            otp.getOtp()
        );

        String status = emailService.sendSimpleMail(emailDetails);

        logger.info(status);

        return this.authenticateUser(new LoginRequest(user.getEmail(), registerRequest.getPassword()));
    }

    @Operation(summary = "Login user")
    @ApiResponses({
      @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = JwtResponse.class)),
      }),
    })  
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
            userDetails.getId(),
            userDetails.getEmail(),
            roles));
    }

    @Operation(summary = "Verify email")
    @ApiResponses({
      @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = MessageResponse.class)),
      }),
      @ApiResponse(responseCode = "400", content = {
        @Content(schema = @Schema(implementation = MessageResponse.class)),
      }),
      @ApiResponse(responseCode = "400", content = {
        @Content(schema = @Schema(implementation = ResourceNotFoundException.class)),
      }),
    })
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody VerifyEmailRequest verifyEmailRequest, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // Authenication authentication = SecurityContextHolder.getContext().getAuthentication();

        // UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        UUID id = userDetails.getId();

        UserOtp otp = userOtpRepository.findByUserId(id)
            .orElseThrow(() -> new ResourceNotFoundException("otp not found"));

        if (TimeUtils.isExpired(otp.getExpiry())) {
            return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: OTP is expired"));
        }

        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("user not found"));

        user.setStatus(UserStatusEnum.ACTIVE);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Email verified successfully"));
    } 
    

}
