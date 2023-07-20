package com.singlesoft.web.service;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

import com.singlesoft.web.jwt.CustomUserDetails;
import com.singlesoft.web.model.User;
import com.singlesoft.web.repository.UserRepository;

@Component
public class UserDetailServiceImpl implements UserDetailsService{
	
	private final UserRepository userRepository;
	

	public UserDetailServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findByName(name);
		if(user.isEmpty()) {
			throw new UsernameNotFoundException("User Not Found:"+user+"\n");
		}
		return new CustomUserDetails(user);
	}

}
