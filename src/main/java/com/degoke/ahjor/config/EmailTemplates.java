package com.degoke.ahjor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;

@Configuration
public class EmailTemplates {
    @Bean
    public SimpleMailMessage templateSimpleMessage() {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setText(
            "<h1>Welcome to ahjor</h1><h2>use this code to verify your email address</h2><p%s</p>"
        );

        return message;
    }
}
