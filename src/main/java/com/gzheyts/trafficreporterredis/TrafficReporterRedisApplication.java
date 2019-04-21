package com.gzheyts.trafficreporterredis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
public class TrafficReporterRedisApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrafficReporterRedisApplication.class, args);
	}

}
