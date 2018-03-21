package com.wizarpos.im.core.db.entity;

import java.util.Date;

/**
 * 用户信息表．加入缓存，可选
 * @author lizhou
 */
public class MUser extends BaseId {
	/** 用户名称 */
	private String userName;
	/** 别名 */
	private String alias;
	/** 用户邮箱 */
	private String email;
	/** 密码 */
	private String password;
	/** 盐 */
	private String salt;
	/** 密码算法 */
	private String alg;
	/** 用户所在的企业id */
	private Long corpId;
	/** 用户最新签名消息 */
	private Long shortMsgId;
	/** 用户logo */
	private String logo;

	/** 生日．公元纪年 */
	private Date birthday;
	/** 性别 N none M male F female */
	private char sex = 'N';
	/** 用户身份证号码 */
	private String cardNo;
	/** 用户手机 */
	private String mobildNo;
	/** 注册时间 */
	private Date registerTime;

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public String getAlg() {
		return alg;
	}
	public void setAlg(String alg) {
		this.alg = alg;
	}
	public Long getCorpId() {
		return corpId;
	}
	public void setCorpId(Long corpId) {
		this.corpId = corpId;
	}
	public Long getShortMsgId() {
		return shortMsgId;
	}
	public void setShortMsgId(Long shortMsgId) {
		this.shortMsgId = shortMsgId;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public char getSex() {
		return sex;
	}
	public void setSex(char sex) {
		this.sex = sex;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getMobildNo() {
		return mobildNo;
	}
	public void setMobildNo(String mobildNo) {
		this.mobildNo = mobildNo;
	}
	public Date getRegisterTime() {
		return registerTime;
	}
	public void setRegisterTime(Date registerTime) {
		this.registerTime = registerTime;
	}


}
