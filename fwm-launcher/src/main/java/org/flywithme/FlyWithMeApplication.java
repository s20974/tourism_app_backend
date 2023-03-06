package org.flywithme;

import org.flywithme.entity.Role;
import org.flywithme.entity.Roles;
import org.flywithme.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@SpringBootApplication
@EnableScheduling
public class FlyWithMeApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlyWithMeApplication.class,args);
    }

    @Bean
    public CommandLineRunner settingRoles(RoleRepository repo) {
        return args -> {
            var roles = repo.findAll();
            if(roles.size() != 2) {
                repo.saveAll(List.of(Role.builder().roles(Roles.USER).build(), Role.builder().roles(Roles.ADMIN).build()));
            }
        };
    }
}
