package cn.wkp.rabbitmq.newest.delayqueue.ttldlx;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import cn.wkp.rabbitmq.util.ConnectionUtil;

/**
 * 
 * @ClassName: Send
 * @Description: 通过ttl+dlx实现延迟队列. 
 * @author wangkangpeng
 * @date: 2019年4月21日 下午9:58:32
 */
public class Send {

	public static void main(String[] args) throws Exception {
		Connection connection = ConnectionUtil.getConnection();
		Channel channel = connection.createChannel();
		//声明一个交换机，做死信交换机用
		channel.exchangeDeclare("delay_exchange_5s", "direct", true, false, null);
		//声明一个队列，做死信队列用
		channel.queueDeclare("delay_queue_5s", true, false, false, null);
		//队列绑定到交换机上
		channel.queueBind("delay_queue_5s", "delay_exchange_5s", "q5s");
		
		channel.exchangeDeclare("work_exchange", "direct", true, false, null);
		Map<String, Object> arguments=new HashMap<String, Object>();
		arguments.put("x-message-ttl" , 5000);//设置消息有效期1秒，过期后变成私信消息，然后进入DLX
		arguments.put("x-dead-letter-exchange" , "delay_exchange_5s");//设置DLX
		//为队列normal_queue 添加DLX
		channel.queueDeclare("work_queue_5s", true, false, false, arguments);
		channel.queueBind("work_queue_5s", "work_exchange", "q5s");
		
		String message = ConnectionUtil.formatDate(new Date())+",延迟五秒的消息";
		channel.basicPublish("work_exchange", "q5s", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
		System.out.println("发送消:"+message);
		
		channel.close();
		connection.close();
	}
}
