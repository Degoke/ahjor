package com.degoke.ahjor.entity;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_otp")
@Getter
@Setter
public class UserOtp {

    @Id 
    @GeneratedValue(strategy = GenerationType.UUID) 
    private UUID id;

    private String otp;

    private Date expiry;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    public UserOtp() {}

    public UserOtp(String otp, Date expiry) {
        this.otp = otp;
        this.expiry = expiry;
    }
}
