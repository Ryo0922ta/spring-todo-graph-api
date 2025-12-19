package com.example.demo.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.converter.UserConverter;
import com.example.demo.model.dto.UserDTO;
import com.example.demo.model.entity.User;

@SpringBootTest
@Transactional //テスト後に自動ロールバック
@ActiveProfiles("test")
public class UserServiceTest {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserConverter userConverter;

	@Test
	void TestSaveUser() {
		UserDTO userDto = new UserDTO.Builder()
				.userId(null)
				.username("test")
				.email("test@sample.com")
				.role("user")
				.build();
		System.out.println("UserDto : " + userDto);
		User user = userConverter.toUserEntity(userDto);
		userRepository.saveUser(user);
	}

}
