package com.kverchi.diary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@ComponentScan("com.kverchi.diary.controller")
@ComponentScan("com.kverchi.diary.repository")
@EntityScan("com.kverchi.diary.model.entity")
public class DnpRestApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(DnpRestApplication.class, args);
	}

	@Bean
	public RequestContextListener requestContextListener() {
		return new RequestContextListener();
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				//registry.addMapping("/**").allowedOrigins(/*Specific route*/);
				registry.addMapping("/**").allowedMethods("GET","POST","PUT","PATCH","DELETE");
			}
		};
	}
}
