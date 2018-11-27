package com.aswishes.im.commons.mvc.service;

import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aswishes.im.commons.exception.ImException;
import com.aswishes.im.commons.exception.UserNotFoundException;
import com.aswishes.im.commons.mvc.dao.UserDao;
import com.aswishes.im.commons.mvc.entity.MUser;
import com.aswishes.im.commons.util.CommonUtils;
import com.aswishes.im.commons.util.DateUtils;
import com.aswishes.im.commons.util.ImConstants;
import com.aswishes.im.commons.util.ImStatus;
import com.aswishes.spring.service.AbstractService;

@Service
public class UserService extends AbstractService<MUser> {
	@Autowired
	private UserDao userDao;
	@Autowired
	private CorporationService corporationService;
	@Override
	public void setDao() {
		this.dao = userDao;
	}

	/**
	 * 计算用户密码．算法格式固定
	 * @param password 计算前密码
	 * @param salt　盐
	 * @return　计算后密码
	 */
	public String calPassword(String password, String salt) {
		return DigestUtils.sha256Hex(password + salt);
	}

	@Transactional
	public void login(String username, String password, String ipv4) {
		MUser user = userDao.get(username);
		if (user == null) {
			throw new UserNotFoundException(ImStatus.S_USER_NOT_FOUND).logArgs(username);
		}
		String storePassword = user.getPassword();
		String tempPassword = calPassword(password, user.getSalt());
		if (!tempPassword.equals(storePassword)) {
			throw new ImException(ImStatus.S_USERNAME_OR_PASSWORD_ERROR).logArgs(username);
		}
		if (user.getStatus() == MUser.STATUS_LOCKED) {
			throw new ImException(ImStatus.S_USER_LOCKED).logArgs(username);
		}
		if (user.getStatus() == MUser.STATUS_DISABLED) {
			throw new ImException(ImStatus.S_USER_DISABLED).logArgs(username);
		}
		if (user.getStatus() == MUser.STATUS_CANCELED) {
			throw new ImException(ImStatus.S_USER_CANCELED).logArgs(username);
		}
		String token = CommonUtils.randomString(32);
		Date loginTime = new Date();
		Date expiryTime = DateUtils.addMinutes(loginTime, 30);
		userDao.updateUserBehavior(user.getId(), token, expiryTime, loginTime, ipv4);
	}

	@Transactional
	public void modifyPassword(String username, String oldPassword, String newPassword) {
		MUser user = userDao.get(username);
		if (user == null) {
			throw new UserNotFoundException(ImStatus.S_USER_NOT_FOUND).logArgs(username);
		}
		String tempPassword = calPassword(oldPassword, user.getSalt());
		if (!user.getPassword().equals(tempPassword)) {
			throw new UserNotFoundException(ImStatus.S_OLD_PASSWORD_ERROR);
		}
		String salt = CommonUtils.randomString(32);
		String storePassword = calPassword(newPassword, salt);
		userDao.modifyPassword(user.getId(), storePassword, salt);
	}

	@Transactional
	public void modifyPassword(String username, String newPassword) {
		MUser user = userDao.get(username);
		if (user == null) {
			throw new UserNotFoundException(ImStatus.S_USER_NOT_FOUND).logArgs(username);
		}
		String salt = CommonUtils.randomString(32);
		String storePassword = calPassword(newPassword, salt);
		userDao.modifyPassword(user.getId(), storePassword, salt);
	}

	@Transactional
	public void addUser(String username, String password, Long corpId, char sex) {
		if (corpId == null) {
			corpId = corporationService.getOfficial().getId();
		}
		String salt = CommonUtils.randomString(32);
		String storePassword = calPassword(password, salt);
		MUser user = new MUser();
		user.setUserName(username);
		user.setAlias(username);
		user.setPassword(storePassword);
		user.setSalt(salt);
		user.setAlg(ImConstants.HASH_SHA256);
		user.setStatus(MUser.STATUS_NORMAL);
		user.setCorpId(corpId);
		user.setSex(sex);

		user.setRegisterTime(new Date());
		Long userId = userDao.register(user);

		userDao.addUserBehavior(userId);
	}
}
