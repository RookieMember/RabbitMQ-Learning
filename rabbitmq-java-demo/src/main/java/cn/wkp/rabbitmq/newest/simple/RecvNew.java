package cn.wkp.rabbitmq.newest.simple;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import cn.wkp.rabbitmq.util.ConnectionUtil;

public class RecvNew {

	private final static String QUEUE_NAME = "test_queue";

	public static void main(String[] argv) throws Exception {

		// 获取到连接以及mq通道
		Connection connection = ConnectionUtil.getConnection();
		Channel channel = connection.createChannel();

		// 声明队列
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);

		// 定义队列的消费者
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				System.out.println("新的消费者api收到消息:" + new String(body));
			}
		};
		// 监听队列
		channel.basicConsume(QUEUE_NAME, true, consumer);
		
		//上面方法的第二个参数设为false就不会报下面异常了
		//com.rabbitmq.client.AlreadyClosedException: channel is already closed due to channel error; protocol method: #method<channel.close>(reply-code=406, reply-text=PRECONDITION_FAILED - unknown delivery tag 1, class-id=60, method-id=80)
//				channel.basicConsume(QUEUE_NAME, true, consumer);
	}
}