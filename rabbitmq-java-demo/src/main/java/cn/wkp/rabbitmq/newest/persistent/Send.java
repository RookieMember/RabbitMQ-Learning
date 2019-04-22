package cn.wkp.rabbitmq.newest.persistent;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import cn.wkp.rabbitmq.util.ConnectionUtil;

public class Send {

    private final static String EXCHANGE_NAME = "persistent_exchange";

    public static void main(String[] argv) throws Exception {
        // 获取到连接以及mq通道
        Connection connection = ConnectionUtil.getConnection();
        // 从连接中创建通道
        Channel channel = connection.createChannel();

        // 声明交换机-持久化，非自动删除
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout", true,false,null);

        // 消息内容
    	String message = "这是一条持久化消息";
    	BasicProperties props= MessageProperties.PERSISTENT_TEXT_PLAIN;
//    	BasicProperties props=new BasicProperties().builder().deliveryMode(2).build();
		channel.basicPublish(EXCHANGE_NAME, "", props, message.getBytes());
    	System.out.println("Sent message:" + message);
        //关闭通道和连接
        channel.close();
        connection.close();
    }
}