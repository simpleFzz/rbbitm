package cn.fzz.controller;


import cn.fzz.fanout.FanoutSender;
import cn.fzz.hello.DefaultSender;
import cn.fzz.qos.QosSender;
import cn.fzz.topic.TopicSender;
import cn.fzz.delay.DelaySender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/rabbit")
public class RabbitTest {

    @Autowired
    private DefaultSender defaultSender;
    @Autowired
    private TopicSender topicSender;
    @Autowired
    private FanoutSender fanoutSender;
    @Autowired
    private QosSender qosSender;
    @Autowired
    private DelaySender delaySender;


    /**
     * 普通类型测试
     */
    @GetMapping("/hello")
    public void hello() { //mq的消息发送
        defaultSender.send("hellomsg!");
    }

    /**
     * topic exchange类型rabbitmq测试
     */
    @GetMapping("/topicTest")
    public void topicTest() {
        topicSender.send();
    }

    /**
     * fanout exchange类型rabbitmq测试
     */
    @GetMapping("/fanoutTest")
    public void fanoutTest() {
        fanoutSender.send("hellomsg:OK");
    }

    @GetMapping("/qosTest")
    public void qosTest(){
        qosSender.pushMsg();
    }

    @GetMapping("/delayTest")
    public void delayTest(){
        delaySender.sendDelay("delay");
    }
}
