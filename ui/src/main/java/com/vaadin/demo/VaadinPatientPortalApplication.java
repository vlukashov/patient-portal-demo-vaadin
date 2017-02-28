package com.vaadin.demo;

import com.vaadin.demo.service.DBInitService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class VaadinPatientPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(VaadinPatientPortalApplication.class, args);
    }

    @Bean
    CommandLineRunner init(DBInitService service) {
        return strings -> service.initDatabase();
    }


}
