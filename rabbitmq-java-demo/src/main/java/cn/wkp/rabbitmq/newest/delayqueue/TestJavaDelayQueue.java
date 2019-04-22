package cn.wkp.rabbitmq.newest.delayqueue;

import java.util.Date;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @ClassName: TestJavaDelayQueue
 * @Description: DelayQueue延迟队列. 
 * @author wangkangpeng
 * @date: 2019年4月14日 下午3:38:04
 */
public class TestJavaDelayQueue {

	class DelayMessage implements Delayed {

		private int id;
		private String body; // 消息内容
		private long excuteTime;// 延迟时长，这个是必须的属性因为要按照这个判断延时时长。

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}

		public long getExcuteTime() {
			return excuteTime;
		}

		public void setExcuteTime(long excuteTime) {
			this.excuteTime = excuteTime;
		}

		public DelayMessage(int id, String body, long delayTime) {
			this.id = id;
			this.body = body;
			this.excuteTime = TimeUnit.NANOSECONDS.convert(delayTime, TimeUnit.MILLISECONDS) + System.nanoTime();
		}

		@Override
		public int compareTo(Delayed o) {
			DelayMessage msg = (DelayMessage) o;  
	        return Integer.valueOf(this.id) > Integer.valueOf(msg.id) ? 1  
	                : (Integer.valueOf(this.id) < Integer.valueOf(msg.id) ? -1 : 0);  
		}

		@Override
		public long getDelay(TimeUnit unit) {
			return unit.convert(this.excuteTime - System.nanoTime(), TimeUnit.NANOSECONDS); 
		}

	}
	
	class Consumer implements Runnable {  
	    // 延时队列 ,消费者从其中获取消息进行消费  
	    private DelayQueue<DelayMessage> queue;  
	  
	    public Consumer(DelayQueue<DelayMessage> queue) {  
	        this.queue = queue;  
	    }  
	  
	    @Override  
	    public void run() {  
	        while (true) {  
	            try {  
	            	DelayMessage take = queue.take();  
	                System.out.println("消费消息id：" + take.getId() + " 消息体：" + take.getBody()+",当前时间:"+new Date().toLocaleString());  
	            } catch (InterruptedException e) {  
	                e.printStackTrace();  
	            }  
	        }  
	    }
	}

	public static void main(String[] args) {
		TestJavaDelayQueue test = new TestJavaDelayQueue();
		DelayQueue<DelayMessage> queue = new DelayQueue<DelayMessage>();
		 // 添加延时消息,m1 延时3s    
		DelayMessage m1 = test.new DelayMessage(1, new Date().toLocaleString(), 3000);    
        // 添加延时消息,m2 延时10s    
		DelayMessage m2 = test.new DelayMessage(2,  new Date().toLocaleString(), 10000);    
        //将延时消息放到延时队列中  
        queue.offer(m2);    
        queue.offer(m1);
        
        ExecutorService exec = Executors.newFixedThreadPool(1);  
        exec.execute(test.new Consumer(queue));  
        exec.shutdown();  
	}
	
	
}
