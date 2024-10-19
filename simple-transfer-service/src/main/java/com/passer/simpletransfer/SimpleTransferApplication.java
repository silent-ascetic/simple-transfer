package com.passer.simpletransfer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@EnableAsync
@ServletComponentScan
@SpringBootApplication
@EnableConfigurationProperties
public class SimpleTransferApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleTransferApplication.class, args);
    }

}
