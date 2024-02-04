package com.degoke.ahjor.service;

import com.degoke.ahjor.dto.EmailDetails;

public interface EmailService {
    String sendSimpleMail(EmailDetails details);

    String sendMailWithAttachment(EmailDetails details);
}
