### 重点记录
1. `ctx.writeAndFlush`只会从当前的`handler`位置开始，往前找`outbound`执行；
2. `ctx.pipeline().writeAndFlush`与`ctx.channel().writeAndFlush`会从`tail`的位置开始，往前找`outbound`执行。


### 参考资料
[一文搞懂Netty中Handler的执行顺序](https://blog.csdn.net/zhengchao1991/article/details/103583766)