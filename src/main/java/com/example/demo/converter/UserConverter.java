package com.example.demo.converter;

import org.springframework.stereotype.Component;

import com.example.demo.model.dto.UserDTO;
import com.example.demo.model.entity.User;

@Component
public class UserConverter {

	public UserDTO toUserDTO(User userEntity) {
		return new UserDTO.Builder()
				.userId(userEntity.getUserId())
				.username(userEntity.getUserName())
				.email(userEntity.getEmail())
				.role(userEntity.getRole()).build();

	}

	public User toUserEntity(UserDTO userdto) {
		return new User.Builder()
				.userId(userdto.getUserId())
				.username(userdto.getUserName())
				.email(userdto.getEmail())
				.role(userdto.getRole()).build();
	}

}
