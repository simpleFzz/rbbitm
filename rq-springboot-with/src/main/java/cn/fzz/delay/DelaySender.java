package cn.fzz.delay;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import cn.fzz.RmConst;

@Component
public class DelaySender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(String msg) {
        String sendMsg = msg +"---"+ System.currentTimeMillis();;
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        System.out.println("Sender : " + sendMsg+"---correlationDataId:"+correlationData.getId());
        System.out.println("DelaySenderTime:"+System.currentTimeMillis());
        //TODO 普通消息处理
        //this.rabbitTemplate.convertAndSend(RmConst.QUEUE_HELLO, sendMsg);
        //TODO 消息处理--(消费者处理时，有手动应答)
        this.rabbitTemplate.convertAndSend("delay_exchange", "delayQueue",(Object) sendMsg,new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setHeader("x-cn.fzz.delay",60000);
                //message.getMessageProperties().setDeliveryMode(MessageProperties.DEFAULT_DELIVERY_MODE);
                return message;
            }
        });
    }

    public  void sendDelay(String msg){
        String sendMsg = msg +"---"+ System.currentTimeMillis();;
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        System.out.println("Sender : " + sendMsg+"---correlationDataId:"+correlationData.getId());
        System.out.println("SenderDl time:"+ LocalDateTime.now().toString());
        this.rabbitTemplate.convertAndSend("DL_QUEUE", (Object) sendMsg, correlationData);
    }
}
