package com.projek.projek.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projek.projek.model.Image;
import com.projek.projek.model.User;
import com.projek.projek.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	public User findByUsernameAndPassword(String username, String password) {
		User user = userRepository.findByUsernameAndPassword(username, password);
		if(user != null) {
			return user;
		}
		return null;
	}
	
	public User findById(Long id) {
		return userRepository.findById(id).get();
	}
	
	public User save(User user) {
		return userRepository.saveAndFlush(user);
	}
}