package io.github.hoanghonghuy.taskly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TasklyApplication {

	public static void main(String[] args) {
		SpringApplication.run(TasklyApplication.class, args);
	}

}
