### WebSocket简介
WebSocket 是 HTML5 开始提供的一种浏览器与服务器间进行全双工通信的网络技术，WebSocket 的特点如下：
- 单一的TCP连接，采用全双工模式通信：
- 对代理、防火墙和路由器透明；
- 无头部信息、Cookie和身份验证；
- 无安全开销：
- 通过 "ping/pong" 帧保持链路激活：
- 服务器可以主动传递消息给客户端，不再需要客户端轮询。

### WebSocket背景
WebSocket设计出来的目的就是要取代轮询和 Comet 技术，使客户端浏览器具备像 C/S 架构下桌面系统一样的实时通信能力。
浏览器通过 JavaScript 向服务器发出建立 WebSocket 连接的请求，连接建立以后，客户端和服务器端可以通过 TCP 连接直接交换数据。
因为 WebSocket 连接本质上就是一个 TCP 连接，所以在数据传输的稳定性和数据传输量的大小方面，和轮询以及 Comet 技术相比，具有很大的性能优势。

### WebSocket建立
建立 WebSocket 连接时，需要通过客户端或者浏览器发出握手请求，请求消息示例如下所示:
```
GET /chat HTTP/1.1
Host: server.example.com
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==
Origin: http://example.com
Sec-WebSoeket-Protoco1: chat, superchat 
Sec-WebSocket-Version: 13
```
为了建立一个 WebSocket 连接，客户端浏览器首先要向服务器发起一个HTTP请求，这个清求和通常的HTTP请求不同，包含了一些附加头信息，
其中附加头信息 "Upgrade: WebSocket" 表明这是一个申请协议升级的HTTP请求。

服务器端解析这些附加的头信息，然后生成应答信息返回给客户端，客户端和服务器端的 WebSocket 连接就建立起来了，双方可以通过这个连接通道自由地传递信息，
并旦这个连接会持续存在直到客户端或者服务器端的某一方主动关闭连接。

服务端返冋给客户端的应答消息如下所示：
```
HTTP/1.1 101 Switching Protocols
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Accept: s3pPLMBlTxaQ9kYGzzhZRbK+xOo-
Sec-WebSocket-Protocol: chat
```
请求消息中的 Sec-WebSocket-Key 是随机的，服务器端会用这些数据来构造出一个SHA-1的信息摘要，把 Sec-WebSocket-Key 加上一个魔幻字符串 `258EAFA5-E914-47DA-95CA-C5AB0DC85BI1`。
使用 SHA-1 加密,然后进行 BASE-64 编码,将结果作为 Sec-WebSocket-Accept 头部的值，返回给客户端。

### WebSocket关闭
为关闭 WebSocket 连接，客户端和服务端需要通过一个安全的方法关闭底层TCP连接以及TLS会话。底层的TCP连接，在正常情况下，应该首先由服务器关闭。
在异常情况下（例如在一个合理的时间周期后没有接收到服务器的 TCP Close）,客户端可以发起 TCP Close。
因此，当服务器被指示关闭WebSocket连接时，它应该立即发起一个 TCP Close 操作，客户端应该等待服务器的 TCP Close。

WebSocket的握手关闭消息带有一个状态码和一个可选的关闭原因，它必须按照协议要求发送一个Close控制帧，当对端接收到关闭控制帧指令时，需要主动关闭 WebSocket 连接。