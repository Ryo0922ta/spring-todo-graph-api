package com.example.demo.repository.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.demo.mapper.UserMapper;
import com.example.demo.model.entity.User;
import com.example.demo.repository.UserRepository;

@Repository
public class UserRepositoryImpl implements UserRepository {

	private final UserMapper userMapper;

	public UserRepositoryImpl(UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	@Override
	public List<User> findAllUsers() {
		List<User> userList = userMapper.selectAllUser();
		return userList;
	}

	@Override
	public User findUserByEmail(String email) {
		User user = userMapper.selectUserByEmail(email);
		return user;
	}

	@Override
	public User findUserById(Integer userId) {
		User user = userMapper.selectUserById(userId);
		return user;
	}

	@Override
	public void saveUser(User user) {
		userMapper.saveUser(user);
	}

}
