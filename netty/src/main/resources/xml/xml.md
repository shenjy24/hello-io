## Netty HTTP 开发
例子参考 https://github.com/shenjy24/jackal-io 项目 netty/protocol/http 目录下的示例。

## Netty HTTP+XML 协议栈开发
由于HTTP协议的通用性，很多异构系统间的通信交互采用HTTP协议，通过HTTP协议承载业务数据进行消息交互，例如非常流行的HTTP+XML或者RESTful+JSON。

在Java领域，最常用的HTTP协议栈就是基于Servlet规范的Tomcat等Web容器，由于谷歌等业界大佬的强力推荐，Jetty等轻量级的Web容器也得到了广泛的应用。

很多基于HTTP的应用都是后台应用，HTTP仅仅是承载数据交换的一个通道，是一个载体而不是Web容器，在这种场景下，一般不需要类似Tomcat这样的重量级Web容器。

在网络安全日益严峻的今天，重量级的Web容器由于功能繁杂，会存在很多安全漏洞，典型的如Tomcat。如果你的客户是安全敏感型的，意味着你需要为Web容器做很多安全加固工作去修补这些漏洞，然而你并没有使用到这些功能，这回带来开发和维护成本的增加。在这种场景下，一个更加轻量级的HTTP协议栈是个更好的选择。

### 开发场景介绍
作为一个示例程序，我们先模拟一个简单的用户订购系统。客户端填写订单，通过HTTP客户端向服务端发送订购请求，请求消息放在HTTP消息体中，以XML承载，即采用HTTP+XML的方式进行通信。HTTP服务端接收到订购请求后，对订单请求进行修改，然后通过HTTP+XML的方式返回应答消息。

双方采用HTTP1.1协议，连接类型为CLOSE方式，即双方交互完成，由HTTP服务端主动关闭链路，随后客户端也关闭链路并退出。

### 高效的XML绑定框架JiBx
#### 核心概念
- Unmarshal(数据分解) : 将XML文件转换成Java对象。
- Marshal(数据编排) : 将Java对象编排成规范的XML文件。

#### 生成XML和Java对象的绑定关系
(1) 从百度云下载插件  

链接: [https://pan.baidu.com/s/1ML21R54xawecs_VkM9GnsQ](https://pan.baidu.com/s/1ML21R54xawecs_VkM9GnsQ)    
密码: qbuo

将下载后插件放在`/Users/shenjiayun/Documents/tool`目录下

(2) 进入`target/classes`目录执行如下命令

```
java -cp /Users/shenjiayun/Documents/tool/jibx/lib/jibx-tools.jar org.jibx.binding.generator.BindGen -t ../../src/main/resources/xml -v com.jonas.serialization.xml.pojo.Address  com.jonas.serialization.xml.pojo.Customer com.jonas.serialization.xml.pojo.Order com.jonas.serialization.xml.pojo.OrderFactory com.jonas.serialization.xml.pojo.Shipping
```

(3) pom文件引入下面依赖以及插件

JiBX依赖包
```xml
<dependency>
    <groupId>org.jibx</groupId>
    <artifactId>jibx-bind</artifactId>
    <version>1.2.5</version>
</dependency>
<dependency>
    <groupId>org.jibx</groupId>
    <artifactId>jibx-run</artifactId>
    <version>1.2.5</version>
</dependency>
<dependency>
    <groupId>org.jibx</groupId>
    <artifactId>jibx-extras</artifactId>
    <version>1.2.5</version>
</dependency>
<dependency>
    <groupId>org.jibx</groupId>
    <artifactId>jibx-schema</artifactId>
    <version>1.2.5</version>
</dependency>
<dependency>
    <groupId>org.jibx</groupId>
    <artifactId>jibx-tools</artifactId>
    <version>1.2.5</version>
</dependency>
```

绑定插件
```xml
<build>
    <plugins>
        <plugin>
            <!-- 生成jibx class信息 -->
            <groupId>org.jibx</groupId>
            <artifactId>jibx-maven-plugin</artifactId>
            <version>1.3.1</version>
            <configuration>
                <!-- 注意此处的路径，binding文件应在该目录下 --> 
                <schemaBindingDirectory>${basedir}/src/main/resources/xml</schemaBindingDirectory>
                <includeSchemaBindings>
                    <includeSchemaBindings>*binding.xml</includeSchemaBindings>
                </includeSchemaBindings>
                <verbose>true</verbose>
            </configuration>
            <executions>
                <execution>
                    <id>jibx-bind</id>
                    <phase>compile</phase>
                    <!-- 把jibx绑定到了comile编译阶段 -->
                    <goals>
                        <goal>bind</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

(4) 引入如上插件后，再执行`mvn install`即可完成绑定。

### Netty开发
代码示例参考 [https://github.com/shenjy24/jackal-io](https://github.com/shenjy24/jackal-io) 项目 netty/serialization/xml 目录下的示例。