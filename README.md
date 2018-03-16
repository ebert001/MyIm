# Openfire中Domain的作用
Openfire通讯时会使用JID．JID的构成: **UserName** @ **Domain**. 例如：lizhou@im.wizarpos.com

在通讯时Openfire服务器会根据此domain进入路由，以确认用户所在的服务器，并将消息发送到对应的用户．

# MyIM
## 服务器架构
一(消息)中心多(连接)端点，多(消息)中心互联，多(连接)端点隔离

用户地址的构成元素: 端点地址(公网ip 或 局域网ip) + 中心地址(公网ip或局域网ip)

用户有单独的登录中心，专门进行登录工作．登录完成获取令牌，此令牌在各个消息中心通用.

前置动作
* 用户登录
* 请求可用服务器列表(有优先级)

指令类型:
* PING
* 终端离线确认
* 点对点消息
* 点对面消息
* 在线状态
* 文件传输
* 检索离线消息
* 用户签名检索信息
* 好友信息检索
* 好友上线信息检索
* 好友上下线通知

## 通讯报文格式定义 Length + Content
### Content格式定义
指令 + 回执 + 用户 + 目标用户(可能是群组)(地址，类似openfire的domain，通过此地址可以精确的定位到用户连接的端点服务器) + 时间 + 内容


```
xxxx
```

