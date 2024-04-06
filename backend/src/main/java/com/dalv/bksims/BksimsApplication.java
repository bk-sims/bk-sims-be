package com.dalv.bksims;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication()
public class BksimsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BksimsApplication.class, args);
	}

}
