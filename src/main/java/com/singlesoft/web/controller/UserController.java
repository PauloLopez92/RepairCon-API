package com.singlesoft.web.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.singlesoft.web.model.ServiceModel;
import com.singlesoft.web.model.User;
import com.singlesoft.web.repository.ServiceRepository;
import com.singlesoft.web.repository.UserRepository;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
    @Autowired
	private UserRepository userRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private PasswordEncoder encoder;

	@GetMapping
	public ResponseEntity<List<User>> getUsers(HttpServletRequest request){
    	// Check User permission by Id at JWT Token
        String token = request.getHeader("Authorization").substring(7);
        DecodedJWT jwt = JWT.decode(token);
        Long userId = jwt.getClaim("userId").asLong();
        User currentUser = userRepository.findById(userId).orElse(null);

        if (currentUser.getUserType().equals("ADMIN")) {
			// To return NumServices too
			List<User> users = userRepository.findAll();
			for (User user : users) {
				int numServices = serviceRepository.countByUser(user);
				user.setNumServices(numServices);
			}
			// Get all users
			return ResponseEntity.ok(users);
        }else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to create service");
        }
	}
	
	@GetMapping("/verify/")
	public ResponseEntity<Boolean> verifyUser(@RequestParam String username, @RequestParam String pass){
		Optional<User> opuser = userRepository.findByName(username);
		if(opuser.isPresent()) {
			boolean matchpass = encoder.matches(pass, opuser.get().getPassword());
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(matchpass);
		}else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
					}

	}

	@PostMapping
	public ResponseEntity<User> saveUser(HttpServletRequest request,@RequestBody User user){
    	// Check User permission by Id at JWT Token
        String token = request.getHeader("Authorization").substring(7);
        DecodedJWT jwt = JWT.decode(token);
        Long userId = jwt.getClaim("userId").asLong();
        User currentUser = userRepository.findById(userId).orElse(null);
        if (currentUser.getUserType().equals("ADMIN")) {
			// To post new user
			user.setUserType(user.getUserType());
			user.setPassword(encoder.encode(user.getPassword()));
			User savedUser = userRepository.save(user);
			return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        }else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to create service");
        }
	}
	
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(HttpServletRequest request,@PathVariable("id") Long id, @RequestBody User user) {
    	// Check User permission by Id at JWT Token
        String token = request.getHeader("Authorization").substring(7);
        DecodedJWT jwt = JWT.decode(token);
        Long userId = jwt.getClaim("userId").asLong();
        User currentUser = userRepository.findById(userId).orElse(null);
		Optional<User> optionalUser = userRepository.findById(id);
		
		// Check if is a ADMIN
        if (currentUser.getUserType().equals("ADMIN")) {
			// To update user
			if (optionalUser.isPresent()) {
				User updatedUser = optionalUser.get();
				
				if(updatedUser.getUserType().equals("ADMIN") && !updatedUser.getId().equals(currentUser.getId())) {
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to create service");
				}else if (user.getPassword().equals("")) {
					updatedUser.setName(user.getName());
					updatedUser.setUserType(user.getUserType());
				}else {
					updatedUser.setName(user.getName());
					updatedUser.setPassword(encoder.encode(user.getPassword()));
					updatedUser.setUserType(user.getUserType());
				}
				User savedService = userRepository.save(updatedUser);
				return ResponseEntity.ok(savedService);

			} else {
				return ResponseEntity.notFound().build();
			}
        }
        else if (currentUser.getId().equals(id)) {
			if (optionalUser.isPresent()) {

				User updatedUser = optionalUser.get();

				if (user.getPassword().equals("")) {
				updatedUser.setName(user.getName());
				}else {
				updatedUser.setName(user.getName());
				updatedUser.setPassword(encoder.encode(user.getPassword()));
				}
				User savedService = userRepository.save(updatedUser);
				return ResponseEntity.ok(savedService);

			}else {
				return ResponseEntity.notFound().build();
			}
        }
		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to create service");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(HttpServletRequest request,@PathVariable("id") Long id) {
    	// Check User permission by Id at JWT Token
        String token = request.getHeader("Authorization").substring(7);
        DecodedJWT jwt = JWT.decode(token);
        Long userId = jwt.getClaim("userId").asLong();
        User currentUser = userRepository.findById(userId).orElse(null);
        Optional<User> optionalUser = userRepository.findById(id);

        if (currentUser.getUserType().equals("ADMIN") && optionalUser.get().getUserType().equals("USER") || optionalUser.get().getId().equals(currentUser.getId())) {
			// Delete Function
			if (optionalUser.isPresent()) {
				List<ServiceModel> serviceList =  serviceRepository.findByUser(optionalUser.get());
				for(ServiceModel service : serviceList) {
					serviceRepository.delete(service);
				}
				userRepository.delete(optionalUser.get());
				return ResponseEntity.noContent().build();
			} else {
				return ResponseEntity.notFound().build();
			}
        }else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to create service");
        }
	}
	
}
