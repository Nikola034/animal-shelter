package com.animalshelter.activitytracking;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class ActivityTrackingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActivityTrackingApplication.class, args);
    }

    @PostConstruct
    void initTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Belgrade"));
    }
}
