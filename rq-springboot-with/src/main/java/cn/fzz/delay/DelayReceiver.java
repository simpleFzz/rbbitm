package cn.fzz.delay;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**

 *类说明：
 */
@Component
@RabbitListener(queues = "REDIRECT_QUEUE")
public class DelayReceiver {
    @RabbitHandler
    public void process(String msg) {
        System.out.println("DelayReceiver : " + msg);
        //System.out.println("SenderDl time:"+ LocalDateTime.now().toString());

        System.out.println("ReceiverDl Timm:"+ LocalDateTime.now().toString());
    }

}
