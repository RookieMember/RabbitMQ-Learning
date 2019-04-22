package cn.wkp.rabbitmq.newest.consumer.ack;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import cn.wkp.rabbitmq.util.ConnectionUtil;

public class Recv {

	private final static String EXCHANGE_NAME = "ack_exchange";
	private final static String QUEUE_NAME = "ack_queue";

	public static void main(String[] argv) throws Exception {

		// 获取到连接以及mq通道
		Connection connection = ConnectionUtil.getConnection();
		final Channel channel = connection.createChannel();

		// 声明交换机-持久化，非自动删除
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout", true,false,null);
		// 声明队列
		channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        // 绑定队列到交换机
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
		
		// 定义队列的消费者
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				String msg = new String(body);
				//消息序号
				int num=Integer.valueOf(msg.split(":")[1]);
				System.out.println("消费者收到消息:" + new String(body));
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(num==1) {
					System.out.println("消费者收到消息:" + new String(body)+",拒绝了这条消息");
					//requeue为true表示让消息重回队列，放入队列尾部，如果为false则会删除当前消息
					channel.basicNack(envelope.getDeliveryTag(), false, true);
				}else {
					System.out.println("消费者收到消息:" + new String(body)+",接收了这条消息");
					channel.basicAck(envelope.getDeliveryTag(), false);
					channel.basicAck(envelope.getDeliveryTag(), false);
				}
			}
		};
		// 监听队列，设置为手动确认
		channel.basicConsume(QUEUE_NAME, false, consumer);
	}
}