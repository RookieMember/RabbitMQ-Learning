package cn.wkp.rabbitmq.newest.producer.confirm.transaction;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import cn.wkp.rabbitmq.util.ConnectionUtil;

/**
 * 
 * @ClassName: Send
 * @Description: 消费发送方确认机制1：事务模式 
 * @author wangkangpeng
 * @date: 2019年3月24日 下午4:30:33
 */
public class Send {

    private final static String QUEUE_NAME = "transaction_queue";

    public static void main(String[] argv) throws Exception {
        // 获取到连接以及mq通道
        Connection connection = ConnectionUtil.getConnection();
        // 从连接中创建通道
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        
        try {
        	//开启事务
        	channel.txSelect();
			String message = "事务消息";
			channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
			//模拟异常，回滚事务
//			int i=1/0;
			//提交事务
			//注意:如果消息没有
			channel.txCommit();
			System.out.println("发送成功,message:" + message);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("发送失败进行回滚");
			//发生异常，回滚事务
			channel.txRollback();
		}
        //关闭通道和连接
        channel.close();
        connection.close();
    }
}