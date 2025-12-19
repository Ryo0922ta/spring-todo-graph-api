package com.example.demo.model.entity;

public class User {
	private Long userId;
	private String userName;
	private String email;
	private String role;

	public User(Long userId, String userName, String email, String role) {
		this.userId = userId;
		this.userName = userName;
		this.email = email;
		this.role = role;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	//	自前でBuilderを実装
	public static class Builder {
		private Long userId;
		private String userName;
		private String email;
		private String role;

		public Builder userId(Long userId) {
			this.userId = userId;
			return this;
		}

		public Builder username(String userName) {
			this.userName = userName;
			return this;
		}

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public Builder role(String role) {
			this.role = role;
			return this;
		}

		public User build() {
			return new User(userId, userName, email, role);
		}

		public static Builder builder() {
			return new Builder();
		}
	}
}
