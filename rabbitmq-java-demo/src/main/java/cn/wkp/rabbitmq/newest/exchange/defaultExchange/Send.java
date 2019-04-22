package cn.wkp.rabbitmq.newest.exchange.defaultExchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import cn.wkp.rabbitmq.util.ConnectionUtil;

public class Send {

    private final static String QUEUE_NAME = "default_exchange_queue";

    public static void main(String[] argv) throws Exception {
        // 获取到连接以及mq通道
        Connection connection = ConnectionUtil.getConnection();
        // 从连接中创建通道
        Channel channel = connection.createChannel();

        // 声明（创建）队列
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        // 消息内容
        for(int i=0;i<10;i++) {
        	String message = "工作"+i;
        	channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        	System.out.println(" [x] Sent '" + message + "'");
        }
        //关闭通道和连接
        channel.close();
        connection.close();
    }
}