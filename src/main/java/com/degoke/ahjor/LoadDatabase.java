package com.degoke.ahjor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.degoke.ahjor.entity.UserRole;
import com.degoke.ahjor.enums.UserRoleEnum;
import com.degoke.ahjor.repository.UserRoleRepository;


@Configuration
public class LoadDatabase {

    private static Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(UserRoleRepository userRoleRepository) {
        return args -> {
            log.info("Preloading " + userRoleRepository.save(new UserRole(UserRoleEnum.ADMIN)));
            log.info("Preloading " + userRoleRepository.save(new UserRole(UserRoleEnum.USER)));
        };
    }
    
}
