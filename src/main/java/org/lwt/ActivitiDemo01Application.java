package org.lwt;

import org.activiti.spring.boot.EndpointAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

@SpringBootApplication
//@EnableAutoConfiguration
//@ConditionalOnClass(value = {EndpointAutoConfiguration.class})
public class ActivitiDemo01Application {

	public static void main(String[] args) {
		SpringApplication.run(ActivitiDemo01Application.class, args);
	}
}
