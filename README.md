## SpringCloud可以解决怎样的问题
* SpringCloud是一套微服务解决方案，包括负载均衡，容错，路由策略，限流，其核心组建netfilx包括
	* 内置Eureka注册中心，用于服务发现和注册
	* 内置ZUUL接口网关和OPS
	* Hystrix服务保护框架
	* Ribbon客户端负载均衡
* 

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
	* 填写配置文件application
	* 编写接口
	* 然后在启动类里面加上`@EnableEurekaClient`就好了
* 5.服务调用者（消费者）
	* 导入依赖(和提供者的依赖一样)
	* 填写配置文件application
	* 调用接口服务提供者的Http接口
	    * 使用真正的IP端口（不会使用注册中心）
	    * 使用服务别名的方式（需要在RestTemplate的Bean上加上`@LoadBalanced`注解来开启负载均衡，负载均衡是使用ribbon实现本地负载均衡）
	* 然后在启动类里面加上`@EnableEurekaClient`就好了
* 6.搭建高可用的Eureka集群
    * 搭建Eureka集群的原理就是相互在对方注册，实现数据的同步(这时要在`eureka.client.service-url.defaultZoone`里注册别的eureka并且需要服务名称
    * 服务在填写`eureka.client.service-url.defaultZoone`的时候用逗号隔开集群中的不同主机