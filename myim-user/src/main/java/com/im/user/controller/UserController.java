package com.im.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.im.commons.mvc.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public void register(String username, String password, char sex) {
		userService.addUser(username, password, null, sex);
	}

	@RequestMapping(value = "/exists", method = RequestMethod.POST)
	public boolean exists(String name) {
		return false;
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public void login(String name, String password) {

	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public void logout(String name) {

	}
}
