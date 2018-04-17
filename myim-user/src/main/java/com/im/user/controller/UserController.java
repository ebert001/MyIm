package com.im.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/user")
public class UserController {

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public void register() {

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
