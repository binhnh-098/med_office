package com.example.med_office;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MedOfficeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedOfficeApplication.class, args);
    }

}
