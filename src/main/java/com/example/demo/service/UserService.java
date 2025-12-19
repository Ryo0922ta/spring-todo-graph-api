package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.converter.UserConverter;
import com.example.demo.model.dto.UserDTO;
import com.example.demo.model.entity.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final UserConverter userConverter;

	public UserService(UserRepository userRepository, UserConverter userConverter) {
		this.userRepository = userRepository;
		this.userConverter = userConverter;
	}

	/**全てのユーザーを取得
	 * @param List<UserDTO>
	 * @return ユーザー情報が含まれているリスト
	 */
	public List<UserDTO> findAllUser() {
		List<User> userList = userRepository.findAllUsers();

		List<UserDTO> userListDto = userList
				.stream()
				.map(user -> userConverter.toUserDTO(user))
				.collect(Collectors.toList());
		return userListDto;

	}

	/**
	 * Eメールから任意のユーザーを取得
	 * @param email
	 * @return　ユーザー情報
	 */
	public UserDTO findUserByEmail(String email) {
		User user = userRepository.findUserByEmail(email);
		if (user != null) {
			return userConverter.toUserDTO(user);
		} else {
			return null;
		}
	}

	/**
	 * ユーザIDから任意のユーザーを取得
	 * @param userId
	 * @return　ユーザー情報
	 */
	public UserDTO findUserById(Integer userId) {
		User user = userRepository.findUserById(userId);
		return userConverter.toUserDTO(user);
	}

	/**
	 * ユーザー情報を保存
	 * @param userDto
	 * @return void
	 */
	public void SaveUser(String userName, String email) {
		UserDTO userDto = new UserDTO.Builder()
				.userId(null)
				.username(userName)
				.email(email)
				.role("user")
				.build();
		User user = userConverter.toUserEntity(userDto);
		userRepository.saveUser(user);
		return;
	}

}
