package com.singlesoft.web.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.singlesoft.web.model.User;
import com.singlesoft.web.repository.UserRepository;

// Class to Create Default User
@Component
public class UserTableInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    public UserTableInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
    	if(!userRepository.findByName("admin").isPresent()) {
			User defaultUser = new User();
			defaultUser.setName("admin");
			defaultUser.setUserType("ADMIN");
			defaultUser.setPassword(encoder.encode("admin"));
			userRepository.save(defaultUser);
    	}else {
			User admin = userRepository.findByName("admin").get();
			admin.setUserType("ADMIN");
			userRepository.save(admin);
    	}
    }
}