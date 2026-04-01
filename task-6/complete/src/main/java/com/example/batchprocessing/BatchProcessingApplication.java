package com.example.batchprocessing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;

@SpringBootApplication
public class BatchProcessingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchProcessingApplication.class, args);
    }
}