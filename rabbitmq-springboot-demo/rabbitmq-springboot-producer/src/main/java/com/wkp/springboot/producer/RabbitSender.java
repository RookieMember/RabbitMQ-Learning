package com.wkp.springboot.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.UUID;

@Service
public class RabbitSender {

    Logger logger = LoggerFactory.getLogger(RabbitSender.class);

    @Resource
    private RabbitTemplate singleRabbitTemplate;
    @Resource
    private ConfirmCallBackHandlerB confirmCallBackHandlerB;
    @Resource
    private ReturnCallBackHandlerB returnCallBackHandlerB;
    @Resource
    private ApplicationContext applicationContext;


    /**
     * 每次获取新的RabbitTemplate
     * @return
     */
    private RabbitTemplate getScopeRabbitTemplate(){
        RabbitTemplate scopeRabbitTemplate = (RabbitTemplate) applicationContext.getBean("scopeRabbitTemplate");
        logger.info("获取新的RabbitTemplate:{}",scopeRabbitTemplate );
        return scopeRabbitTemplate;
    }

    /**
     * 采用多例的RabbitTemplate发送消息方法调用: 构建Message消息<br/>
     * 根据自己的需要添加confirm和return回调
     * @param message
     * @param properties
     * @throws Exception
     */
    public void scopeSend(Object message, Map<String, Object> properties) throws Exception {
        RabbitTemplate rabbitTemplate=getScopeRabbitTemplate();
//        MessageHeaders mhs = new MessageHeaders(properties);
//        Message msg = MessageBuilder.createMessage(message, mhs);
        rabbitTemplate.setConfirmCallback(confirmCallBackHandlerB);
        rabbitTemplate.setReturnCallback(returnCallBackHandlerB);
//        rabbitTemplate.convertAndSend("springboot_topic_exchange", "aaspringboot.info", msg, correlationData);
        //id + 时间戳 全局唯一
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString().replace("-", ""));
        rabbitTemplate.convertAndSend("springboot_topic_exchange", "springboot.info", message, correlationData);
    }

    /**
     * 采用单例的RabbitTemplate发送消息方法调用: 构建Message消息<br/>
     * 使用统一的confirm和return回调
     * @param message
     * @param properties
     * @throws Exception
     */
    public void singleSend(Object message, Map<String, Object> properties) throws Exception {
//        MessageHeaders mhs = new MessageHeaders(properties);
//        Message msg = MessageBuilder.createMessage(message, mhs);
//        singleRabbitTemplate.convertAndSend("springboot_direct_exchange", "direct_Key", msg, correlationData);
//        //id + 时间戳 全局唯一
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString().replace("-", ""));
        singleRabbitTemplate.convertAndSend("springboot_direct_exchange", "direct_Key", message,correlationData);
    }
}
