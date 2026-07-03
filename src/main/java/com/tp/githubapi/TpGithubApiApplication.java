package com.tp.githubapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TpGithubApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TpGithubApiApplication.class, args);
    }
}
