
/** 基础数据库文件 */
/** 企业表 */
create table m_corporation (
	id bigint not null auto_incremaent primary key comment '主键',
	name varchar(120) not null comment '企业名称',
	create_time date not null comment '添加时间',
	
	unique key `uk_name` (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/** 用户表 */
create table m_user (
	id bigint not null auto_increment primary key comment '主键，用户id',
	name varchar(50) not null comment '用户名称',
	alias varchar(50) not null comment '用户别名',
	password varchar(128) not null comment '用户密码',
	salt varchar(128) not null comment '密码盐',
	corp_id bigint not null comment '用户企业id',
	short_msg_id bigint comment '用户最新签名id',
	logo varchar(50) comment '用户logo',
	
	mobile_no varchar(20) comment '用户手机',
	card_no varchar(32) comment '用户身份证信息',
	register_time date not null comment '注册时间', 
	
	unique key `uk_name` (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/** 用户签名消息 */
create table m_user_short_msg (
	id bigint not null auto_increment primary key comment '主键，用户id',
	user_id bigint not null comment '用户id',
	short_msg varchar(200) not null comment '用户签名消息',
	create_time date not null comment '注册时间', 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/** 用户行为表 */
create table m_user_behavior (
	id bigint not null auto_increment primary key comment '主键',
	user_id bigint not null comment '用户id',
	presence tinyint not null default 0 comment '用户状态. 0 离线 1 在线 2 空闲 3 忙碌',
	net_type tinyint not null default 0 comment '网络类型．0 未知 1 有线 2 WIFI 3 2G 4 3G 5 4G 6 5G ',
	device_type tinyint not null default 0 comment '设备类型. 0 未知 1 pc-win 2 pc-linux 3 pc-apple 4 mo-apple 5 pad-apple 6 mo-android 7 pad-android',
	token varchar(64) not null comment '用户登录成功获得的令牌',
	expiry date not null comment '用户令牌过期时间',
	last_login_time datetime comment '最近登录时间',
	
	unique key `uk_user_id` (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/** 用户好友分组 */
create table m_user_group (
	id bigint not null auto_increment primary key comment '主键',
	user_id bigint not null comment '用户id',
	name varchar(50) not null comment '分组名称',
	create_time datetime comment '创建时间',
	
	unique key `uk_user_id_name` (user_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/** 用户分组中拥有的好友 */
create table r_group_user (
	id bigint not null auto_increment primary key comment '主键',
	group_id bigint not null comment '用户组id',
	friend_id bigint not null comment '好友id',
	friend_alias varchar(50) not null comment '好友别名', 
	friend_name varchar(50) comment '好友名称',
	create_time datetime comment '创建时间',
	
	unique key `uk_friend_user` (user_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/** 用户群组 */
create table m_muc (
	id bigint not null auto_increment primary key comment '主键',
	creator_id bigint not null comment '群组创建者id',
	name varchar(50) not null comment '分组名称',
	logo varchar(50) comment '群组logo',
	
	create_time datetime comment '创建时间',
	
	unique key `uk_user_id_name` (creator_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/** 用户群组中拥有的好友 */
create table r_muc_user (
	id bigint not null auto_increment primary key comment '主键',
	muc_id bigint not null comment '用户群组id',
	friend_id bigint not null comment '好友id',
	friend_alias varchar(50) not null comment '好友别名', 
	friend_name varchar(50) comment '好友名称',
	create_time datetime comment '创建时间',
	
	unique key `uk_friend_user` (user_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/** 离线消息 */
create table m_offline_msg (
	id bigint not null auto_increment primary key comment '主键',
	user_id bigint not null comment '用户id',
	muc_id bigint comment '聊天室id',
	friend_id bigint not null comment '好友id', 
	msg text not null comment '消息，最大2个字节长度',
	send_time datetime comment '发送时间',
	create_time datetime comment '消息记录入库时间',	
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/** 已发送消息(正常情况下是不用记录的，但是保留设计).聊天室消息，不需要记录好友id */
create table m_offline_msg (
	id bigint not null auto_increment primary key comment '主键',
	user_id bigint not null comment '用户id',
	muc_id bigint comment '聊天室id',
	friend_id bigint not null comment '好友id', 
	msg text not null comment '消息，最大2个字节长度',
	send_time datetime comment '发送时间',
	create_time datetime comment '消息记录入库时间',	
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

