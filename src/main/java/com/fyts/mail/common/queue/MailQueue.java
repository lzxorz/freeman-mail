package com.fyts.mail.common.queue;

import com.fyts.mail.entity.Mail;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 单例邮件队列
 *
 */
public class MailQueue {
	 /**队列大小*/
    static final int QUEUE_MAX_SIZE   = 1000;

    static BlockingQueue<Mail> blockingQueue = new LinkedBlockingQueue<Mail>(QUEUE_MAX_SIZE);
    
    /**
     * 私有的默认构造子，保证外界无法直接实例化
     */
    private MailQueue(){};

    /**
     * 静态内部类，延迟加载
     */
    private static class SingletonHolder{
        /**Ø
         * 静态初始化器，由JVM来保证线程安全
         */
		private  static MailQueue queue = new MailQueue();
    }

    /**
     * 获取队列
     */
    public static MailQueue getMailQueue(){
        return SingletonHolder.queue;
    }

    /**
     * 获取队列大小
     */
    public int size() {
        return blockingQueue.size();
    }

    /**
     * 生产者添加对象到队列
     */
    public  void  produce(Mail mail) throws InterruptedException {
    	blockingQueue.put(mail);
    }

    /**
     * 消费者从队列获取对象
     */
    public Mail consume() throws InterruptedException {
        return blockingQueue.take();
    }

}
