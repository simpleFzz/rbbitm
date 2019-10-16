package cn.fzz.delay;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**

 *类说明：
 */
@Component
//@RabbitListener(queues = "delayQueue")
public class DelayReceiver {
    @RabbitHandler
    public void process(String msg) {
        System.out.println("DelayReceiver : " + msg);
        System.out.println("DelayReceiverTimm:"+System.currentTimeMillis());
        System.out.println("删除订单");
    }

}
