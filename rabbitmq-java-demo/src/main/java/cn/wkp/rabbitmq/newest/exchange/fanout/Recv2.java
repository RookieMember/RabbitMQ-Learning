package cn.wkp.rabbitmq.newest.exchange.fanout;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import cn.wkp.rabbitmq.util.ConnectionUtil;

public class Recv2 {

	private final static String EXCHANGE_NAME = "fanout_exchange";
	private final static String QUEUE_NAME = "fan_queue2";

	public static void main(String[] argv) throws Exception {

		// 获取到连接以及mq通道
		Connection connection = ConnectionUtil.getConnection();
		final Channel channel = connection.createChannel();

		 // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
		// 声明队列
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // 绑定队列到交换机
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
		
		//指该消费者在接收到队列里的消息但没有返回确认结果之前,它不会将新的消息分发给它。
		channel.basicQos(1);

		// 定义队列的消费者
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				System.out.println("消费者2收到消息:" + new String(body));
				//消费者手动发送ack应答
				channel.basicAck(envelope.getDeliveryTag(), false);
			}
		};
		// 监听队列
		channel.basicConsume(QUEUE_NAME, false, consumer);
	}
}