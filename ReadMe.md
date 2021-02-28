# 基本描述
- MyBatis的日志类的一个实现，增加了基础的参数替换功能。
- 将查询结果以表格的形式输出。

# 效果
![效果展示](images/show.jpg)


![效果展示](images/show1.png)

# 注意

1. 输出的级别为INFO级别

2. 默认不输出结果集

# 使用

## 输出结果集

配置文件中增加如下配置

~~~properties
# 输出结果
top.wmgx.mybatisPlusLogPlus.enableShowResult=true
# 每列最大宽度（默认10）
top.wmgx.mybatisPlusLogPlus.maxWidth=10
# 显示前n条记录（默认30）
top.wmgx.mybatisPlusLogPlus.topLine=30
# 显示前n列（默认9）
top.wmgx.mybatisPlusLogPlus.topColumn=9
~~~

使用方法一要在启动类上加入`@MybatisPlusLogPlusAutoConfig`注解

使用方法二不用可以直接使用。

## 方法一

1. 将target目录下的jar包放到项目目录下
2. 在pom文件中引入
~~~xml
    <dependency>
            <groupId>top.wmgx</groupId>
            <artifactId>MybatisPlusLogPlus</artifactId>
            <version>1.0</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/{存放的项目下的目录}/MybatisPlusLogPlus-1.3.jar</systemPath>
        </dependency>
~~~
3. 配置文件
~~~properties
mybatis-plus.configuration.log-impl=top.wmgx.MybatisPlusLogPlus
~~~
## 方法二

- 将MybatisPlusLogPlus.java直接拷贝到项目中，配置文件中写这个类的路径即可
- 代码很简单，自行修改即可。
- SqlFormatter为Hutool的工具类，可以不用，目的是输出美观的sql。
- 为方便拷贝，将所有的代码都在一个类中写了。