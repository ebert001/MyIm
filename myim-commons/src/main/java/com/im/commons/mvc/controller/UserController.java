package com.im.commons.mvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.im.commons.mvc.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;

	@RequestMapping("/register")
	public void register(String username, String password, char sex, Long corpId) {
		userService.addUser(username, password, corpId, sex);
	}

	@RequestMapping("/modifyPassword")
	public void modifyPassword(String username, String oldPassword, String newPassword) {
		userService.modifyPassword(username, oldPassword, newPassword);
	}

	@RequestMapping("/resetPassword")
	public void resetPassword(String username, String newPassword) {
		userService.modifyPassword(username, newPassword);
	}
}
