package com.ruijie.cpu;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class CpuApplication {
    public static void main(String[] args) {
        SpringApplication.run(CpuApplication.class, args);
    }
}
