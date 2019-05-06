package com.wkp.springboot.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.annotation.Resource;

@Configuration
public class ProducerRabbitConfig {

    Logger logger = LoggerFactory.getLogger(ProducerRabbitConfig.class);

    @Resource
    private ConnectionFactory connectionFactory;
    @Resource
    private ConfirmCallBackHandlerA confirmCallBackHandlerA;
    @Resource
    private ReturnCallBackHandlerA returnCallBackHandlerA;

    /**
     * setConfirmCallback: Only one ConfirmCallback is supported by each RabbitTemplate(源码中可以看到)
     * setReturnCallback: Only one ReturnCallback is supported by each RabbitTemplate(源码中可以看到)
     * 解决RabbitTemplate多次设置confirmCallBack和returnCallBack会上面报异常的方法:
     * 1:将RabbitTemplate设置为单例,并设置两个回调方法，这样所有采用此RabbitTemplate发送消息的都会应用这两个回调
     * 2:将RabbitTemplate设置为多例,后面发送消息时根据业务需要自己添加相应的回调方法
     */

    /**
     * 单例的RabbitTemplate<br/>
     * 并且设置了confirm和return回调
     * @return
     */
    @Bean
    public RabbitTemplate singleRabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setConfirmCallback(confirmCallBackHandlerA);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback(returnCallBackHandlerA);
        return rabbitTemplate;
    }

    /**
     * 多例的RabbitTemplate
     * @return
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate scopeRabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }
}
