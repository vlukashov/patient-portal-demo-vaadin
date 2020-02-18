package com.vaadin.demo;

import com.vaadin.demo.service.DBInitService;
import com.vaadin.demo.ui.security.VaadinSessionSecurityContextHolderStrategy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.context.SecurityContextHolder;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class PatientPortalApplication {

    @Bean
    CommandLineRunner init(DBInitService service) {
        return strings -> service.initDatabase();
    }

    @Configuration
    @EnableGlobalMethodSecurity(securedEnabled = true)
    public static class SecurityConfiguration extends GlobalMethodSecurityConfiguration {

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            // Create a default account
            auth.inMemoryAuthentication()
                    .withUser("admin")
                    .password("password")
                    .roles("ADMIN").and()
                    .withUser("user")
                    .password("password")
                    .roles("USER");
        }

        @Bean
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return authenticationManager();
        }

        static {
            // Use a custom SecurityContextHolderStrategy
            SecurityContextHolder.setStrategyName(VaadinSessionSecurityContextHolderStrategy.class.getName());
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(PatientPortalApplication.class, args);
    }

}
