package com.wkp.springboot.consumer;

import com.rabbitmq.client.Channel;
import com.wkp.common.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.util.ErrorHandler;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

@Configuration
public class ConsumerRabbitConfig {

    Logger logger = LoggerFactory.getLogger(ConsumerRabbitConfig.class);
    @Resource
    private ConnectionFactory connectionFactory;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("springboot_direct_exchange", true, false);
    }

    @Bean
    public Queue queueA() {
        return new Queue("direct_queue", true);
    }

    @Bean
    public Binding bindingA() {
        return BindingBuilder.bind(queueA()).to(exchange()).with("direct_Key");
    }

    //上面@Bean注解和下面的@RabbitListener都是可以自动声明绑定，交换机和队列的
    /**
     * 注解式消息消费及(交换机，队列)声明绑定
     *
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @org.springframework.amqp.rabbit.annotation.Queue(value = "topic_queue",
                    durable = "true"),
            exchange = @org.springframework.amqp.rabbit.annotation.Exchange(value = "springboot_topic_exchange",
                    durable = "true",
                    type = "topic",
                    ignoreDeclarationExceptions = "true"),
            key = "springboot.*"
    )
    )
    @RabbitHandler
    public void comsumeB(Message message, Channel channel) throws IOException {
        logger.info("RabbitListener-消费者B收到消息:{}", message.getPayload());
        Long deliveryTag = (Long)message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        /**
         * 消费如果抛出了异常，处理方式:
         * 如果是手动签收模式，可以try catch包括，catch到了异常进行重回队列或者进行落库等操作
         * 如果是自动签收，默认会重回队列，然后一直循环重复消费。可以设置消息重新投递(设置最大投递次数，投递时间间隔，达到最大投递次数后是否重回队列等)
         */
        //模拟消费时抛出异常
//        int i=1/0;
        //手工ACK
        channel.basicAck(deliveryTag, false);
    }

    @RabbitListener(queues = "direct_queue")
    @RabbitHandler
    public void comsumeA(@Payload Order order,
                         Channel channel,
                         @Headers Map<String, Object> headers) throws IOException {
        logger.info("RabbitListener-消费者A收到消息:{}", order);
        Long deliveryTag = (Long)headers.get(AmqpHeaders.DELIVERY_TAG);
        channel.basicAck(deliveryTag, false);
    }

    //通过SimpleMessageListenerContainer消息监听容器消费消息，可以同时监听多个队列，可以设置消费者标签策略，预抓取数量，并行消费数量，消息转换器等
//    @Bean
//    public SimpleMessageListenerContainer simpleMessageListenerContainer() {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
//        container.setConcurrentConsumers(2);//并行消费者数量
//        container.setMaxConcurrentConsumers(5);//最大消费者数量
//        container.setPrefetchCount(10);
//        container.setQueueNames("direct_queue", "topic_queue");
//        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);//手动签收
//        //消费者标签策略
//        container.setConsumerTagStrategy(new ConsumerTagStrategy() {
//            @Override
//            public String createConsumerTag(String queue) {
//                return queue + "_tag";
//            }
//        });
////        container.setMessageConverter(new Jackson2JsonMessageConverter());//消息转换器
//        container.setErrorHandler(new ErrorHandler() {
//            @Override
//            public void handleError(Throwable t) {
//
//            }
//        });
//        container.setChannelAwareMessageListener(new ChannelAwareMessageListener() {
//            @Override
//            public void onMessage(org.springframework.amqp.core.Message message, Channel channel) throws Exception {
//                MessageProperties props = message.getMessageProperties();
//                logger.info("MessageListenerContainer-消费者收到消息:{},consumerQueue:{},consumerTag:{}", new String(message.getBody()),props.getConsumerQueue(),props.getConsumerTag());
//                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//            }
//        });
//        return container;
//    }

    //我们可以自定义RabbitListenerContainerFactory并设置消息转换器等，如果不设置，系统会自动帮我们创建一个SimpleRabbitListenerContainerFactory
//    @Bean
//    public RabbitListenerContainerFactory rabbitListenerContainerFactory(){
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory);
//        factory.setConcurrentConsumers(2);//并行消费者数量
//        factory.setMaxConcurrentConsumers(5);//最大消费者数量
//        factory.setPrefetchCount(10);
//        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);//手动签收
//        //消费者标签策略
//        factory.setConsumerTagStrategy(new ConsumerTagStrategy() {
//            @Override
//            public String createConsumerTag(String queue) {
//                return queue + "_tag";
//            }
//        });
//        factory.setMessageConverter(new Jackson2JsonMessageConverter());//消息转换器
//        factory.setErrorHandler(new ErrorHandler() {
//            @Override
//            public void handleError(Throwable t) {
//
//            }
//        });
//        return factory;
//    }
}
