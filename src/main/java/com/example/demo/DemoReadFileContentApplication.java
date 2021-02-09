package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoReadFileContentApplication{

	public static void main(String[] args) {
		// why do I need to do this? (fails in maven build without it.
		//TomcatURLStreamHandlerFactory.disable();
		SpringApplication.run(DemoReadFileContentApplication.class, args);
	}

//	@Bean
//	public ServletWebServerFactory servletWebServerFactory() {
//		return new TomcatServletWebServerFactory();
//	}
}
