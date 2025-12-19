package com.example.demo.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.converter.UserConverter;
import com.example.demo.model.dto.UserDTO;
import com.example.demo.model.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@Transactional //テスト後に自動ロールバック
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	void TestSelectAll() {
		List<User> Result = userRepository.findAllUsers();
		//
		//		assertNotNull(Result);
		//		assertFalse(Result.isEmpty());

		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println(
					mapper.writerWithDefaultPrettyPrinter().writeValueAsString(Result));
		} catch (JsonProcessingException e) {
			// 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	@Test
	void TestSelectByEmail() {
		String email = "@example.com";
		User Result = userRepository.findUserByEmail(email);
		//
		//		assertNotNull(Result);
		//		assertFalse(Result.isEmpty());

		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println(
					mapper.writerWithDefaultPrettyPrinter().writeValueAsString(Result));
		} catch (JsonProcessingException e) {
			// 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	@Test
	void TestUserSave() {
		UserDTO dto = new UserDTO.Builder()
				.userId(null)
				.username("佐藤test")
				.email("tarotest123@example.com")
				.role("user")
				.build();

		UserConverter userConverter = new UserConverter();
		User user = userConverter.toUserEntity(dto);
		userRepository.saveUser(user);

		System.out.println("TestUserSave() 実行してます。");
		assertAll(
				() -> assertNotNull(user),
				() -> assertNotNull(user.getUserId()),
				() -> assertNotNull(user.getUserName()),
				() -> assertNotNull(user.getEmail()));

		List<User> Result = userRepository.findAllUsers();

		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println(
					mapper.writerWithDefaultPrettyPrinter().writeValueAsString(Result));
		} catch (JsonProcessingException e) {
			// 自動生成された catch ブロック
			e.printStackTrace();
		}

	};

}
