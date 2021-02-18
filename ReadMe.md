# 基本描述
MyBatis的日志类的一个实现，增加了基础的参数替换功能。
# 效果
![效果展示](images/show.jpg)
# 注意
1 输出的级别为INFO级别

# 使用
## 方法一
1. 将target目录下的jar包放到项目目录下
2. 在pom文件中引入
~~~xml
    <dependency>
            <groupId>top.wmgx</groupId>
            <artifactId>MybatisPlusLogPlus</artifactId>
            <version>1.0</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/{存放的项目下的目录}/MybatisPlusLogPlus-1.0.jar</systemPath>
        </dependency>
~~~
3. 配置文件
~~~properties
mybatis-plus.configuration.log-impl=top.wmgx.MybatisPlusLogPlus
~~~
## 方法二
将MybatisPlusLogPlus.java直接拷贝到项目中，配置文件中写这个类的路径即可
代码很简单，自行修改即可。
SqlFormatter为Hutool的工具类，可以不用，目的是输出美观的sql。