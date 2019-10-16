package cn.fzz.qos;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**

 *类说明：
 */
@Component
public class QosSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final int THREAD_NUM = 100;

    private static int thread_id = 0;

    public void pushMsg(){
        System.out.println("初始化线程。。。");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < THREAD_NUM; i++) {
            System.out.println("初始化第"+i+"个线程");
            new Thread(new RunThread(countDownLatch)).start();
        }
        //线程启动
        System.out.println("启动线程");
        countDownLatch.countDown();
    }

    private class RunThread implements Runnable{
        private final CountDownLatch startLatch;
        public RunThread(CountDownLatch countDownLatch){
            this.startLatch = countDownLatch;
        }
        @Override
        public void run() {
            try {
                startLatch.await();
                thread_id +=1;
                send("第"+thread_id+"个thread");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String msg) {
        String sendMsg = msg +"---"+ System.currentTimeMillis();;
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        System.out.println("Sender : " + sendMsg+"---correlationDataId:"+correlationData.getId());
        //TODO 普通消息处理
        //this.rabbitTemplate.convertAndSend(RmConst.QUEUE_HELLO, sendMsg);
        //TODO 消息处理--(消费者处理时，有手动应答)
        //this.rabbitTemplate.convertAndSend(RmConst.QUEUE_USER, (Object) sendMsg, correlationData);
        this.rabbitTemplate.convertAndSend("qosQueue",sendMsg);
    }

}
