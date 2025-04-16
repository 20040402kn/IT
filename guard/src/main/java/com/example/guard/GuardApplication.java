package com.example.guard;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.guard.demos.web.service.*;
import org.springframework.context.annotation.Bean;

@SpringBootApplication

public class GuardApplication  {

    @Autowired
    private ExamService examService;

    public static void main(String[] args) {
        SpringApplication.run(GuardApplication.class, args);
    }

}
