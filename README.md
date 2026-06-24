# Litepress

轻量级 Java 开发框架，脱离 Spring 也能轻松开发。内置持久层、MVC、网络通信、RPC、事件总线等能力。

**要求：JDK 21+**

## 快速开始

### 1. 引入依赖

```xml
<dependency>
    <groupId>io.github.java-zengguang</groupId>
    <artifactId>litepress-boot</artifactId>
    <version>${litepress.version}</version>
</dependency>
```

### 2. 创建启动类

```java
@TargetEvn("dev")
@ProjectRootPath("/tomcat")
@ScanPackages("com.example.app")
public class MyApp extends BaseWebAppBoot {

    public MyApp(Class clazz, Integer port) {
        super(clazz, port);
    }

    public static void main(String[] args) {
        MyApp app = new MyApp(MyApp.class, 8080);
        app.init();
    }

    @Override
    public void config() {
        // 自定义配置
    }
}
```

### 3. 编写配置文件

文件名格式：`{环境}_BeanConfig.xml`，如 `dev_BeanConfig.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans>
    <bean id="optionDB" class="io.github.java_zengguang.litepress.core.bean.entity.OptionDB">
        <property name="url" type="java.lang.String">jdbc:mysql://localhost:3306/mydb</property>
        <!-- ... -->
    </bean>

    <bean id="MVCOption" class="io.github.java_zengguang.litepress.web.entity.MVCOption">
        <!-- ... -->
    </bean>
</beans>
```

## 模块说明

| 模块 | 说明 |
|------|------|
| `litepress-boot` | 应用启动器，包扫描与初始化 |
| `litepress-core` | 核心工具、配置、加解密 |
| `litepress-web` | 基于 Jetty 的 MVC 框架 |
| `litepress-db` | 持久层，SQL 解析、事务、DAO |
| `litepress-network` | 基于 Netty 的网络通信 |
| `litepress-direction` | RPC 远程调用 |
| `litepress-router` | 基于 Zookeeper 的服务注册与发现 |
| `litepress-event` | 事件总线（本地 / RocketMQ / ZeroMQ / Redis） |
| `litepress-reaction` | 异步处理与信号量管理 |
| `litepress-cache` | 缓存（内存 / MongoDB / Infinispan） |
| `litepress-task` | 基于 Quartz 的定时任务 |
| `litepress-chain` | 数据链路与对比 |
| `litepress-label` | 基于规则的标签引擎 |
| `litepress-ml` | 机器学习（ND4J / DeepLearning4J） |

## 构建与发布

```bash
# 编译
mvn clean compile

# 发布到公司内网（默认）
mvn deploy

# 发布到 Maven Central
mvn deploy -P central
```

## 参与贡献

1. Fork 本仓库
2. 新建 `Feat_xxx` 分支
3. 提交代码
4. 新建 Pull Request

## License

[MIT](LICENSE)
