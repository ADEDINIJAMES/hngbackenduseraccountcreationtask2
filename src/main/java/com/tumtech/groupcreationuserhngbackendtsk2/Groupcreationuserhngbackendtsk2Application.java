package com.tumtech.groupcreationuserhngbackendtsk2;

import com.tumtech.groupcreationuserhngbackendtsk2.serviceImplementation.UserServiceImplementation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan(basePackages = "com.tumtech.groupcreationuserhngbackendtsk2")
public class Groupcreationuserhngbackendtsk2Application {

	public static void main(String[] args) {
		SpringApplication.run(Groupcreationuserhngbackendtsk2Application.class, args);
	}


}
