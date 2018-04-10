package com.im.commons.db.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.im.commons.db.dao.UserDao;
import com.im.commons.db.entity.MUser;
import com.im.commons.util.CommonUtils;

@Service
public class UserService  {
	@Autowired
	private UserDao userDao;

	public void login(String username, String password) {
//		userDao.getObjectBy(mapper, restrictions)
	}

	@Transactional
	public void resetPassword(String username) {

	}

	public void addUser(String username, String password) {
		String salt = CommonUtils.randomString(32);
		String storePassword = DigestUtils.sha256Hex(password + salt);

	}
}
