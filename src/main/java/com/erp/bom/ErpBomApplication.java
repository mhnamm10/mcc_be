package com.erp.bom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ErpBomApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErpBomApplication.class, args);
    }

}
