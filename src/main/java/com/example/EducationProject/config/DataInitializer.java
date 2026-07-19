package com.example.EducationProject.config;

import com.example.EducationProject.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.EducationProject.entity.Role;


    @Configuration
    public class DataInitializer {

        @Bean
        CommandLineRunner initRoles(RoleRepository roleRepository) {
            return args -> {

                if (roleRepository.findByName("ROLE_USER").isEmpty()) {
                    roleRepository.save(new Role("ROLE_USER"));
                }

                if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
                    roleRepository.save(new Role("ROLE_ADMIN"));
                }

            };
        }
    }
