package cn.wkp.rabbitmq.newest.dlx;

import java.io.IOException;
import java.util.Date;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import cn.wkp.rabbitmq.util.ConnectionUtil;

public class RecvDLX {

	public static void main(String[] argv) throws Exception {
		Connection connection = ConnectionUtil.getConnection();
		final Channel channel = connection.createChannel();

		channel.exchangeDeclare("dlx_exchange", "topic", true, false, null);
		channel.queueDeclare("dlx_queue", true, false, false, null);
		channel.queueBind("dlx_queue", "dlx_exchange", "dlx.*");

		// 指该消费者在接收到队列里的消息但没有返回确认结果之前,它不会将新的消息分发给它。
		channel.basicQos(1);

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				System.out.println("消费者收到消息:" + new String(body)+",当前时间:"+ConnectionUtil.formatDate(new Date()));
				// 消费者手动发送ack应答
				channel.basicAck(envelope.getDeliveryTag(), false);
			}
		};
		System.out.println("消费死信队列中的消息======================");
		// 监听队列
		channel.basicConsume("dlx_queue", false, consumer);
	}
}