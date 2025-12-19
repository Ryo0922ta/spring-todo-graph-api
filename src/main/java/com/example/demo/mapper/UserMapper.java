package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.model.entity.User;

@Mapper
public interface UserMapper {

	//ユーザーの全件取得	
	public List<User> selectAllUser();

	//emailから任意のユーザーを取得
	public User selectUserByEmail(String email);

	//userIdから任意のユーザーを取得
	public User selectUserById(Integer userId);

	//ユーザーの登録
	public Integer saveUser(User user);
}
