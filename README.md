# RabbitMQ-Learning
  这个主要是自己写RabbitMQ博客过程中的一些demo示例，之前一直没有整理上传到GitHub上面，最近整理上传了一下。博客地址：https://blog.csdn.net/u012988901 ，欢迎交流访问。
  关于RabbitMQ的安装配置可以参考 https://blog.csdn.net/u012988901/article/details/87653771 。  
  
## 内容介绍  
### rabbitmq-java-demo
rabbitmq-java-demo是RabbitMQ的java客户端amqp-client的demo程序示例，里面主要介绍了ampq-client的各种操作，下面对各个包做个简单介绍：  
* cn.wkp.rabbitmq.util.ConnectionUtil 客户端连接工具类
* cn.wkp.rabbitmq.newest.simple 该包下为第一个demo示例——hello world，里面介绍了新老两种消费方式代码，对应博客：https://blog.csdn.net/u012988901/article/details/87977756
* cn.wkp.rabbitmq.newest.work 该包下为工作队列示例，下面的fair子包为工作队列的公平分发模式，对应博客：https://blog.csdn.net/u012988901/article/details/87980339
* cn.wkp.rabbitmq.newest.exchange.defaultExchange 和 cn.wkp.rabbitmq.newest.exchange.fanout，分别是默认交换机和fanout交换机的例子，对应博客：
https://blog.csdn.net/u012988901/article/details/88143105
* cn.wkp.rabbitmq.newest.exchange.direct 是direct交换机示例，对应博客：https://blog.csdn.net/u012988901/article/details/88418833
* cn.wkp.rabbitmq.newest.exchange.topic 是topic交换机示例，对应博客：https://blog.csdn.net/u012988901/article/details/88652313
* cn.wkp.rabbitmq.newest.persistent 和 cn.wkp.rabbitmq.newest.consumer.ack 分别是消息持久化和消费者ack确认、重回队列的示例，对应博客：https://blog.csdn.net/u012988901/article/details/88763405
* cn.wkp.rabbitmq.newest.producer.confirm 介绍了生产者确认两种方式：事务和publisher confirm，对应博客：https://blog.csdn.net/u012988901/article/details/88778966
* cn.wkp.rabbitmq.newest.producer.returnlistener 介绍了mandatory和return机制，对应博客：https://blog.csdn.net/u012988901/article/details/88931180
* cn.wkp.rabbitmq.newest.ttl 和 cn.wkp.rabbitmq.newest.dlx 分别介绍了消息的有效期和死信交换机，对应博客：https://blog.csdn.net/u012988901/article/details/88958654
* cn.wkp.rabbitmq.newest.delayqueue该包下是延迟队列的实现，子包ttldlx是通过ttl+dlx实现的延迟队列，子包plugin是通过插件实现的，TestJavaDelayQueue是Java的延迟队列，对应博客:https://blog.csdn.net/u012988901/article/details/89296451
### rabbitmq-spring-demo
该项目是RabbitMQ与Spring的整合代码示例，对应博客：https://blog.csdn.net/u012988901/article/details/89499634
### rabbitmq-springboot-demo
该项目是RabbitMQ与SpringBoot的整合代码，其子项目common为公共工程，producer是消息生产者，consumer是消息消费者，对应博客：https://blog.csdn.net/u012988901/article/details/89673618
