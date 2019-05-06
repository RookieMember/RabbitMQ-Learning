package com.wkp.springboot.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Service;

/**
 * 做消息可靠性投递
 */
@Service
public class ConfirmCallBackHandlerA implements RabbitTemplate.ConfirmCallback {

    Logger logger = LoggerFactory.getLogger(ConfirmCallBackHandlerA.class);

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        //根据回调的correlationDataId判断发送的哪条消息投递成功，哪条没有成功
        String correlationDataId =null;
        if(correlationData!=null){
            correlationDataId = correlationData.getId();
        }
        if(ack){
            logger.info("A-CONFIRM机制:消息投递成功:correlationId:{}",correlationDataId);
        }else{
            //TODO 消息投递失败,重新投递
            logger.warn("A-CONFIRM机制:消息投递失败:correlationId:{},cause:{}",correlationDataId,cause);
        }
    }
}
