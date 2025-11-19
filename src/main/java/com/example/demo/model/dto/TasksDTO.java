package com.example.demo.model.dto;

public class TasksDTO {
	private Long taskId;

	private String taskName;

	private Integer importance;

	private Integer urgency;

	private Long userId;

	private Long stateId;

	public TasksDTO() {
	}

	public TasksDTO(Long taskId, String taskName, Integer importance, Integer urgency, Long userId, Long stateId) {
		this.taskId = taskId;
		this.taskName = taskName;
		this.importance = importance;
		this.urgency = urgency;
		this.userId = userId;
		this.stateId = stateId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Integer getImportance() {
		return importance;
	}

	public void setImportance(Integer importance) {
		this.importance = importance;
	}

	public Integer getUrgency() {
		return urgency;
	}

	public void setUrgency(Integer urgency) {
		this.urgency = urgency;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getStateId() {
		return stateId;
	}

	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}

}
