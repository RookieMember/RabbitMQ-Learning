package cn.wkp.rabbitmq.newest.delayqueue.plugin;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import cn.wkp.rabbitmq.util.ConnectionUtil;

/**
 * 
 * @ClassName: Send
 * @Description: 通过rabbitmq-delayed-message-exchange插件实现延迟队列. 
 * @author wangkangpeng
 * @date: 2019年4月21日 下午9:58:04
 */
public class Send {

	private final static String EXCHANGE_NAME = "delay_plugin_exchange";
	private final static String ROUTING_KEY = "delay";

	public static void main(String[] argv) throws Exception {
		// 获取到连接以及mq通道
		Connection connection = ConnectionUtil.getConnection();
		// 从连接中创建通道
		Channel channel = connection.createChannel();

		//声明x-delayed-type类型的exchange
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-delayed-type", "direct");
		channel.exchangeDeclare(EXCHANGE_NAME, "x-delayed-message", true, false, args);

		// 消息内容
		for (int i = 0; i < 5; i++) {
			Thread.sleep(1000);
	        Map<String, Object> headers= new HashMap<String, Object>();
	        long delayTime=2000;
	        if(i%2==0) {
	        	delayTime=5000;
	        }
	        headers.put("x-delay",delayTime);//消息延迟时间
			BasicProperties props = new BasicProperties.Builder()
	                .headers(headers).build();
			String message = "序号:" + i + ",时间:" + ConnectionUtil.formatDate(new Date());
			channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, props, message.getBytes());
			System.out.println("Sent message:" + message);
		}
		// 关闭通道和连接
		channel.close();
		connection.close();
	}
}