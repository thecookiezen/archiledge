package com.thecookiezen.archiledger.agenticmemory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.thecookiezen.archiledger")
public class MemoryMcpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemoryMcpServerApplication.class, args);
    }

}
