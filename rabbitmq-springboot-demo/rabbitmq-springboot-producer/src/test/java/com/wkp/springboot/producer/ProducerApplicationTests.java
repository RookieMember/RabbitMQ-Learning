package com.wkp.springboot.producer;

import com.wkp.common.model.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProducerApplicationTests {

    @Resource
    private RabbitSender rabbitSender;
    @Resource
    private RabbitTemplate singleRabbitTemplate;

    @Test
    public void contextLoads() {
    }

    /**
     * 同时发送大量消息
     * @throws Exception
     */
    @Test
    public void test_concurrentSendMsg() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(50);

        AtomicInteger at = new AtomicInteger(0);
        for (int i = 1; i < 10000; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        rabbitSender.singleSend("大量发送消息:"+at.get(), null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    at.incrementAndGet();
                }
            });
        }
        Thread.sleep(100000L);
    }

    /**
     * 测试多例RabbitTemplate发送消息
     * @throws Exception
     */
    @Test
    public void test_scopeSendMsg() throws Exception {
        rabbitSender.scopeSend("多例测试111", null);
//        rabbitSender.scopeSend("多例测试222", null);
        Thread.sleep(10000L);
    }

    /**
     * 测试单例RabbitTemplate发送消息
     * @throws Exception
     */
    @Test
    public void test_singleSendMsg() throws Exception {
        Order order = new Order(1L, "123456", "测试订单");
        rabbitSender.singleSend(order, new HashMap<>());
        Thread.sleep(10000L);
    }

    /**
     * 测试单例RabbitTemplate发送消息无法被路由
     * @throws Exception
     */
    @Test
    public void test_singleSendMsg_NoRoute() throws Exception {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString().replace("-", ""));
        singleRabbitTemplate.convertAndSend("springboot_direct_exchange", "error_key", "无法路由的消息",correlationData);
    }
}
