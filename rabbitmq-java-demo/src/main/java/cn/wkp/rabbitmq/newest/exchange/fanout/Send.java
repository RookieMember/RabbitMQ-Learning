package cn.wkp.rabbitmq.newest.exchange.fanout;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import cn.wkp.rabbitmq.util.ConnectionUtil;

public class Send {

    private final static String EXCHANGE_NAME = "fanout_exchange";

    public static void main(String[] argv) throws Exception {
        // 获取到连接以及mq通道
        Connection connection = ConnectionUtil.getConnection();
        // 从连接中创建通道
        Channel channel = connection.createChannel();

        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        // 消息内容
    	String message = "这是一条fanout类型交换机消息";
    	channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
    	System.out.println("Sent message:" + message);
        //关闭通道和连接
        channel.close();
        connection.close();
    }
}