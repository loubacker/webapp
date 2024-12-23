package com.pushbait.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EntityScan(basePackages = "com.pushbait.webapp.entity")
public class WebAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebAppApplication.class, args);
	}

	@Configuration
	public class DefaultViewConfig implements WebMvcConfigurer {
		@Override
		public void addViewControllers(@NonNull ViewControllerRegistry registry) {
			registry.addViewController("/").setViewName("index");
		}
	}

}