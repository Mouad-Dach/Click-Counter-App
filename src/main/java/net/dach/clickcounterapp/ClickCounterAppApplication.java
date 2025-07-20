package net.dach.clickcounterapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ClickCounterAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClickCounterAppApplication.class, args);
    }
}
