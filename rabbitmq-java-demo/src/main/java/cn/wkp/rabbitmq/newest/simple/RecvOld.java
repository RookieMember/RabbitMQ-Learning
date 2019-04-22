package cn.wkp.rabbitmq.newest.simple;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import cn.wkp.rabbitmq.util.ConnectionUtil;

public class RecvOld {

	private final static String QUEUE_NAME = "test_queue";

	public static void main(String[] argv) throws Exception {

		// 获取到连接以及mq通道
		Connection connection = ConnectionUtil.getConnection();
		Channel channel = connection.createChannel();

		// 声明队列
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);

		// 定义队列的消费者
		QueueingConsumer consumer = new QueueingConsumer(channel);
		// 监听队列
		channel.basicConsume(QUEUE_NAME, true, consumer);
		
		while(true) {
			Delivery delivery = consumer.nextDelivery();
			System.out.println("老的消费者api收到消息:"+new String(delivery.getBody()));
		}
	}
}