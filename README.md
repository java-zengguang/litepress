# databases

#### 介绍
一个自主实现的轻量级开发框架、包含持久层框架、MVC框架、网络通信框架、PRC框架，脱离Spring也可以轻松开发。

#### 平台说明
    要求JDK21+

#### 安装教程

1、安装依赖

           <dependency>
                <groupId>io.github.java_zengguang</groupId>
                <artifactId>litepress-boot</artifactId>
                <version>${litepress.version}</version>
            </dependency>

2、创建启动类

@TargetEvn(value = "dev")
@ProjectRootPath(value = "/tomcat")
@ScanPackages(value = "com.sinosig.zg.smart.admin")
public class SinoSigWebApp extends BaseWebAppBoot {

    public SinoSigWebApp(Class clazz, Integer port) {

        super(clazz, port);
    }

    public static void main(String[] args) {

        SinoSigWebApp sinoSigWebApp = new SinoSigWebApp(SinoSigWebApp.class, 9913);
        sinoSigWebApp.init();

    }

    @Override
    public void config() {

    }
}


3、场景配置文件，配置文件名称为  环境+_BeanConfig.xml

<?xml version="1.0" encoding="UTF-8"?>
<beans>

    <bean id="optionDB" class="io.github.java_zengguang.litepress.core.bean.entity.OptionDB">
        <property name="url" type="java.lang.String">xxx</property>
        ...
    </bean>


    <bean id="MVCOption" class="io.github.java_zengguang.litepress.web.entity.MVCOption">
        ...
    </bean>
</beans>



#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
