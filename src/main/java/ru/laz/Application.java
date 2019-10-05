package ru.laz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;


@EnableScheduling
//@EnableJpaRepositories //redundant with jpa boot starter
@SpringBootApplication
public class Application {


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        File directory = new File("./");
        System.out.println(directory.getAbsolutePath());
    }

}