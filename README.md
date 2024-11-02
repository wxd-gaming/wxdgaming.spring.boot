# wxdgamig.spring.boot

## 更新仓库地址

github https://github.com/orgs/wxd-gaming/repositories<br>
gitee &nbsp;&nbsp;&nbsp;https://gitee.com/wxd-gaming<br>
博客首页 https://www.cnblogs.com/wxd-gameing<br>

## 项目介绍

無心道 基于spring boot的辅助架构

## 模块介绍

| 模块         | 说明                                              | 
|------------|-------------------------------------------------|
| core       | 核心模块，辅助类                                        |
| data       | 数据块                                             |
| data-batis | spring data jpa 基础模块                            |
| data-excel | 基于poi读取excel文件加载到内存数据采用Map映射方式                  |
| data-redis | spring data redis                               |
| message    | 处理proto消息基础模块，proto文件转化pojo。消息的序列化和反序列化         |
| net        | 基于netty实现tcp和websocket服务模块                      |
| rpc        | 基于netty实现rpc远程调用                                |
| script-lua | 基于luajava实现java和lua的互操作模块                       |
| start      | 测试启动模块                                          |
| web        | spring boot web webmvc                          |
| web-client | 基于apache-client 实现httpclient，get，post，post-file |
| web-lua    | 基于luajava实现springboot web 调用lua模块实现web服务        |
