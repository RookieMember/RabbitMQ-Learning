package com.wkp.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * 消息路由失败
 */
public class ReturnCallBackHandler implements RabbitTemplate.ReturnCallback {

    Logger logger = LoggerFactory.getLogger(ConfirmCallBackHandler.class);

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        String msg = new String(message.getBody());
        logger.warn("RETURN机制:消息路由失败,msg:{},replyCode:{},replyText:{},exchange:{},routingKey:{}",msg,replyCode,replyText,exchange,routingKey);
    }

}
