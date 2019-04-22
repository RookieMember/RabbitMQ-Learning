package cn.wkp.rabbitmq.newest.dlx;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import cn.wkp.rabbitmq.util.ConnectionUtil;

public class SendDLX {

	public static void main(String[] args) throws Exception {
		Connection connection = ConnectionUtil.getConnection();
		Channel channel = connection.createChannel();
		//声明一个交换机，做死信交换机用
		channel.exchangeDeclare("dlx_exchange", "topic", true, false, null);
		//声明一个队列，做死信队列用
		channel.queueDeclare("dlx_queue", true, false, false, null);
		//队列绑定到交换机上
		channel.queueBind("dlx_queue", "dlx_exchange", "dlx.*");
		
		channel.exchangeDeclare("normal_exchange", "fanout", true, false, null);
		Map<String, Object> arguments=new HashMap<String, Object>();
		arguments.put("x-message-ttl" , 5000);//设置消息有效期1秒，过期后变成私信消息，然后进入DLX
		arguments.put("x-dead-letter-exchange" , "dlx_exchange");//设置DLX
		arguments.put("x-dead-letter-routing-key" , "dlx.test");//设置DLX的路由键(可以不设置)
		//为队列normal_queue 添加DLX
		channel.queueDeclare("normal_queue", true, false, false, arguments);
		channel.queueBind("normal_queue", "normal_exchange", "");
		
		channel.basicPublish("normal_exchange", "", MessageProperties.PERSISTENT_TEXT_PLAIN, ("测试死信消息").getBytes());
		System.out.println("发送消息时间:"+ConnectionUtil.formatDate(new Date()));
		
		channel.close();
		connection.close();
	}
}
