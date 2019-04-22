package cn.wkp.rabbitmq.newest.ttl;

import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import cn.wkp.rabbitmq.util.ConnectionUtil;

/**
 * 
 * @ClassName: Send
 * @Description: 消息有效期 
 * @author wangkangpeng
 * @date: 2019年4月1日 下午11:28:22
 */
public class Send {

	private final static String EXCHANGE_NAME = "ttl_exchange";
	private final static String QUEUE_NAME = "ttl_queue";

	public static void main(String[] argv) throws Exception {
		// 获取到连接以及mq通道
		Connection connection = ConnectionUtil.getConnection();
		// 从连接中创建通道
		Channel channel = connection.createChannel();

		// 声明交换机
		channel.exchangeDeclare(EXCHANGE_NAME, "direct",true);
		
		//*****1:通过队列设置有效期 2:通过消息属性设置有效期,如果都设置了以较小的为准*****
		//声明队列
		Map<String, Object> arguments=new HashMap<String,Object>();
		//设置队列上所有的消息的有效期,单位为毫秒
		arguments.put("x-message-ttl", 5000);//5秒钟
		channel.queueDeclare(QUEUE_NAME, true, false, false, arguments);
		//绑定
		channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "error");
		
		Builder bd = new AMQP.BasicProperties().builder();
		bd.deliveryMode(2);//持久化
		bd.expiration("100000");//设置消息有效期100秒钟
		BasicProperties pros = bd.build();
		String message = "测试ttl消息";
		channel.basicPublish(EXCHANGE_NAME, "error", true,false, pros, message.getBytes());
		System.out.println("Sent message:" + message);
//		 关闭通道和连接
		channel.close();
		connection.close();
	}
}