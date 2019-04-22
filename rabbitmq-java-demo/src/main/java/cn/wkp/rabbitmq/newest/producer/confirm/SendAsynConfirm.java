package cn.wkp.rabbitmq.newest.producer.confirm;
import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import cn.wkp.rabbitmq.util.ConnectionUtil;
/**
 * 
 * @ClassName:SendAsynConfirm
 * @Description:生产者confirm模式:异步confirm 
 * @author wkp
 * @date 2019年3月29日 下午10:10:01
 */
public class SendAsynConfirm {
 
    private final static String EXCHANGE_NAME = "fanout_exchange";
    //TreeSet是有序集合,元素使用其自然顺序进行排序,拥有存储需要confirm确认的消息序号
    static SortedSet<Long> confirmSet = Collections.synchronizedSortedSet(new TreeSet<Long>());
 
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
    	channel.addConfirmListener(new ConfirmListener() {
    		
    		public void handleNack(long deliveryTag, boolean multiple)
    				throws IOException {
				if (multiple) {
					confirmSet.headSet(deliveryTag + 1).clear();
				} else {
					confirmSet.remove(deliveryTag);
				}
    		}
    		
    		public void handleAck(long deliveryTag, boolean multiple)
    				throws IOException {
    			//confirmSet.headSet(n)方法返回当前集合中小于n的集合
    			if (multiple) {
    				//批量确认:将集合中小于等于当前序号deliveryTag元素的集合清除，表示这批序号的消息都已经被ack了
    				System.out.println("ack批量确认,deliveryTag:"+deliveryTag+",multiple:"+multiple+",当次确认消息序号集合:"+confirmSet.headSet(deliveryTag + 1));
					confirmSet.headSet(deliveryTag + 1).clear();
				} else {
					//单条确认:将当前的deliveryTag从集合中移除
					System.out.println("ack单条确认,deliveryTag:"+deliveryTag+",multiple:"+multiple+",当次确认消息序号:"+deliveryTag);
					confirmSet.remove(deliveryTag);
				}
    			//需要重发消息
    		}
    	});
    	
    	for(int i=0;i<30;i++){
    		String message = "异步confirm消息"+i;
    		//得到下次发送消息的序号
    		long nextPublishSeqNo = channel.getNextPublishSeqNo();
    		channel.basicPublish(EXCHANGE_NAME, "", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
    		//将序号存入集合中
    		confirmSet.add(nextPublishSeqNo);
    	}
        //关闭通道和连接
//        channel.close();
//        connection.close();
    }
}