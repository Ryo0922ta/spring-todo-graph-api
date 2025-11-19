package com.example.demo.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.converter.TaskConverter;
import com.example.demo.model.dto.TasksDTO;
import com.example.demo.model.entity.Tasks;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@Transactional //テスト後に自動ロールバック
public class TasksRepositoryTest {

	@Autowired
	private TaskRepository taskRepository;

	@Test
	void TestSelectAll() {
		List<Tasks> Result = taskRepository.findAllTasks();

		assertNotNull(Result);
		assertFalse(Result.isEmpty());

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
	void TestSelectTask() {
		Long testId = 2L;
		Tasks Result = taskRepository.findTasks(testId);

		assertNotNull(Result);

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
	void TestInsert() {
		TasksDTO dto = new TasksDTO(
				null,
				"test task",
				5,
				5,
				1L,
				1L);

		TaskConverter taskConverter = new TaskConverter();
		Tasks task = taskConverter.toTaskEntity(dto);
		int result = taskRepository.saveTask(task);

		assertEquals(1, result);
	};

}
