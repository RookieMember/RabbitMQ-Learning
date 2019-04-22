package cn.wkp.rabbitmq.newest.producer.confirm;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import cn.wkp.rabbitmq.util.ConnectionUtil;
/**
 * 
 * @ClassName:SendSingleConfirm
 * @Description:生产者confirm模式:单条confirm 
 * @author wkp
 * @date 2019年3月29日 下午10:10:01
 */
public class SendSingleConfirm {
 
    private final static String EXCHANGE_NAME = "fanout_exchange";
 
    public static void main(String[] argv) throws Exception{
        // 获取到连接以及mq通道
        Connection connection = ConnectionUtil.getConnection();
        // 从连接中创建通道
        Channel channel = connection.createChannel();
        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout",true);
        //声明队列
        channel.queueDeclare("test_queue", true, false, false, null); 
        //绑定
        channel.queueBind("test_queue", EXCHANGE_NAME, "");
        
    	channel.confirmSelect();//将信道置为confirm模式
    	
    	channel.confirmSelect();//将信道置为confirm模式
    	String message = "单条confirm消息";
    	channel.basicPublish(EXCHANGE_NAME, "", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
    	if(!channel.waitForConfirms()){
    	    System.out.println("消息发送失败");
    	    //进行重发等操作
    	}
    	System.out.println("消息发送成功");
        //关闭通道和连接
        channel.close();
        connection.close();
    }
}