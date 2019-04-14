# SpringCloud基础与服务治理
* SpringCloud是一套微服务解决方案，包括负载均衡，容错，路由策略，限流，其核心组建netfilx包括
	* 内置Eureka注册中心，用于服务发现和注册
	* 内置ZUUL接口网关和OPS
	* Hystrix服务保护框架
	* Ribbon客户端负载均衡
* 导入集成的SpringCloud依赖**注意版本号，遇见过Zookeeper连上不创建节点的问题**
```
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
<!-- 注意：由于Eureka2.0之后闭源 这里必须要添加， 否者各种依赖有问题 -->
<repositories>
    <repository>
        <id>spring-milestones</id>
        <name>Spring Milestones</name>
        <url>https://repo.spring.io/libs-milestone</url>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
</repositories>

```

---
## 一、服务治理与Eureka 
* 1.RPC的核心思想在于注册中心
* 2.服务治理：**在传统的RPC调用框架里，每个服务与调用者关系复杂**，这时使用服务治理技术，管理每个服务与服务之间的依关系，实现服务的发现与注册，容错等
	* 注册中心用于存放服务地址相关的信息（存放接口地址），SpringCloud支持Euerka，Consul，Zookeeper，Dubbo支持Redis和Zookeeper
	* 一个既服务可以成为提供者，也可以提供消费者 
	* 微服务中的负载均衡称为**本地负载均衡**

* 3.Eureka搭建
	* 导入依赖（需要注意的是，由于Euerka2.0闭源之后，SpringCloud的Euerka版本号不会在更新了，所以需要再引用一个仓库）
	
	```
	<!--SpringCloud eureka-server -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
	```
	仓库
	
	```
	<!-- 注意：由于Eureka2.0之后闭源 这里必须要添加， 否者各种依赖有问题 -->
    <repositories>
        <repository>
            <id>spring-milestones</id>
            <name>Spring Milestones</name>
            <url>https://repo.spring.io/libs-milestone</url>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
    </repositories>
	```
	* 填写配置文件application.properties
	
	```
	server.port=8081
	#注册中心IP
	eureka.instance.hostname=127.0.0.1
	#注册中心地址
	eureka.client.service-url.defaultZoone=http://${eureka.instance.hostname}:${server.port}/eureka/
	#是否需要把自己注册到注册中心上
	eureka.client.register-with-eureka=false
	#是否检索服务信息
	eureka.client.fetch-registry=false
	```
	* 然后在启动类里面加上`@EnableEurekaServer`就好了，访问这个ip和端口就看看见Euerka的OPS
* 4.服务提供者
	* 导入依赖
	
	```xml
    <dependencies>
       <dependency>
           <groupId>org.springframework.cloud</groupId>
           <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
       </dependency>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-web</artifactId>
       </dependency>
    </dependencies>
    ```
	* 填写配置文件application
	
	```
	 server.port=8800
     #服务的名字
     spring.application.name=web-provider
     #注册中心的地址
     eureka.client.service-url.defaultZone=http://127.0.0.1:8081/eureka
	```
	* 编写接口
	* 然后在启动类里面加上`@EnableEurekaClient`就好了
	```java
    @SpringBootApplication
    @EnableEurekaClient
    public class WebProviderApplication {
        public static void main(String[] args) {
            SpringApplication.run(WebProviderApplication.class, args);
        }
    }
    ```
* 5.服务调用者（消费者）
	* 导入依赖(和提供者的依赖一样)
	* 填写配置文件application
	* 调用接口服务提供者的Http接口
	    * 使用真正的IP端口（不会使用注册中心）
	    * 使用服务别名的方式（需要在RestTemplate的Bean上加上`@LoadBalanced`注解来开启负载均衡，负载均衡是使用ribbon实现本地负载均衡）
	    
	```java
    @RestController
    public class ConsumerController {
        @Autowired
        private RestTemplate restTemplate;
    
        @RequestMapping("/test")
        public String test() {
            String providerUrl = "http://web-provider/test";
            return restTemplate.getForObject(providerUrl, String.class);
        }
    }
    ```
	* 然后在启动类里面加上`@EnableEurekaClient`就好了
	
	```java
    @SpringBootApplication
    @EnableEurekaClient
    public class WebConsumerApplication {
        public static void main(String[] args) {
            SpringApplication.run(WebConsumerApplication.class, args);
        }
    
        @Bean
        @LoadBalanced
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }
    ```
* 6.搭建高可用的Eureka集群
    * 搭建Eureka集群的原理就是相互在对方注册，实现数据的同步(这时要在`eureka.client.service-url.defaultZoone`里注册别的eureka并且需要服务名称
    * 服务在填写`eureka.client.service-url.defaultZoone`的时候用逗号隔开集群中的不同主机

* 7.Eureka服务保护机制
    * 防止Eureka客户端和服务端在网络环境较差的情况下，服务可以正常运行，但是服务端错误的将服务剔除的问题
    * 原理：所有的服务会向Eureka定时（默认5s）发送心跳包，Eureka收到心跳包后就会判定服务可用。如果Eureka在一定的时间（默认90s）内没有收到心跳包，就会剔除该服务。但是在短时间内丢失大量心跳包，Eureka就会认为网络不通畅，开启保护机制。
    * 何时开启自我保护机制：本地测试环境建议关闭，生产环境建议开启
    * 如何禁止：
        * 在Server端application.properties里添加：
    
        ```
        #关闭自我保护机制
        eureka.server.enable-self-preservation=false
        #2s检测不可用服务
        eureka.server.eviction-interval-timer-in-ms=2000
        ```
        * 在Client端application.properties里添加：
        
        ```
        #发送心跳包的间隔
        eureka.instance.lease-expiration-duration-in-seconds=1
        #告诉服务端收不到心跳包什么时候就剔除
        eureka.instance.lease-renewal-interval-in-seconds=2
        ```
        
        
## 二、使用Zookeeper替代Eureka作为注册中心
Zookeeper是一个分布式协调工具，使用zookeeper做注册中心的步骤如下
* SpringCloud集成Zookeeper

    * 1.在服务中导入依赖：
    ```xml
    <dependency>
          <groupId>org.springframework.cloud</groupId>
          <artifactId>spring-cloud-starter-zookeeper-discovery</artifactId>
    </dependency>
    ```
    * 2.编写服务的配置文件

    ```
    #注册到Zookeeper
    spring.cloud.zookeeper.connect-string=192.168.3.203:2181
    ``` 
    * 3.在启动类里添加`@EnableDiscoveryClient`注解，就可以启动了
* 在Zookeeper里添加Ribbon负载均衡
    * 和Eureka一样就好了

## 三、使用Consul作为注册中心
* 1.直接下载和启动Consul，默认端口在8500，只要访问这个端口就可以访问OPS
* 2.引入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-discovery</artifactId>
</dependency>
```
* 3.添加参数
```

```
* 4.然后在启动类里面加上`@EnableEurekaClient`就好了

---

# SpringCloud负载均衡
* Ribbon是本地客户端负载均衡器，在获取到所有的服务实例的时候，就会在本地做负载均衡的算法（轮询是通过取余的方式实现的）
* Ribbon与Nginx的区别
    * Ribbon实把所有的实例缓存到本地，通过算法进行负载均衡，适合在微服务RPC调用中使用
    * Nginx是在服务端，客户端所有的请求都交给Nginx实现转发，适合针对于服务器端的

# Feign客户端工具
Feign是一个声明式Http客户端工具，采用接口与注解实现。集成方法如下
* 导入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```
* 创建一个接口，这个接口就就一个远程调用的所有方法，调用接口如下

```java
/**下面的就是web提供者应用名*/
@FeignClient(name = "web-provider")
public interface TestRemoteApi {
    @RequestMapping("/test")
    public String getMessage();
}
```
* 然后就可以在真正需要调用的地方自动注入这个接口，然后调用

```java
@RestController
public class TestController {
    @Autowired
    private TestRemoteApi testRemoteApi;
    @RequestMapping("/test")
    public String test() {
        return testRemoteApi.getMessage();
    }
}
```
* 记得在启动类里添加启动Feign的注解

```java
@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
public class FeignApplication {
    public static void main(String[] args) {
        SpringApplication.run(FeignApplication.class, args);
    }
}
```