package com.singlesoft.web;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@ComponentScan(basePackages = "com.singlesoft.web")
@EntityScan(basePackages = "com.singlesoft.web.model")
@EnableJpaRepositories("com.singlesoft.web.repository")
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
	@Value("${my.app.name}")
	private String appName;
	@PostConstruct
	public void logAppName() {
		System.out.println("\n\nRUNNING APPLICATION:"+appName+"\n\n");
	}
	@Bean
	public PasswordEncoder getPasswordEnconder() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); 
		return encoder;
	}
}