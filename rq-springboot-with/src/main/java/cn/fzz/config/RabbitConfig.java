package cn.fzz.config;

import cn.fzz.RmConst;
import cn.fzz.hello.UserReceiver;
import cn.fzz.qos.QosReceiver;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**

 *类说明：
 */
@Configuration
public class RabbitConfig {

    @Value("${spring.rabbitmq.host}")
    private String addresses;

    @Value("${spring.rabbitmq.port}")
    private String port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

    @Value("${spring.rabbitmq.publisher-confirms}")
    private boolean publisherConfirms;

    @Autowired
    private UserReceiver userReceiver;

    @Autowired
    private QosReceiver qosReceiver;

    //TODO 连接工厂
    @Bean
    public ConnectionFactory connectionFactory() {

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(addresses+":"+port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        //TODO 消息发送确认
        /** 如果要进行消息回调，则这里必须要设置为true */
        connectionFactory.setPublisherConfirms(publisherConfirms);
        return connectionFactory;
    }
    //TODO rabbitAdmin类封装对RabbitMQ的管理操作
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        return new RabbitAdmin(connectionFactory);
    }

    //TODO 使用Template
    @Bean
    public RabbitTemplate newRabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        //TODO 失败通知
        template.setMandatory(true);
        //TODO 发送方确认
        template.setConfirmCallback(confirmCallback());
        //TODO 失败回调
        template.setReturnCallback(returnCallback());
        return template;
    }

    //===============使用了RabbitMQ系统缺省的交换器（direct交换器）==========
    //TODO 申明队列（最简单的方式）
    @Bean
    public Queue helloQueue() {
        return new Queue(RmConst.QUEUE_HELLO);
    }
    @Bean
    public Queue userQueue() { return new Queue(RmConst.QUEUE_USER); }



    //===============以下是验证topic Exchange==========
    @Bean
    public Queue queueEmailMessage() {
        return new Queue(RmConst.QUEUE_TOPIC_EMAIL);
    }

    @Bean
    public Queue queueUserMessages() {
        return new Queue(RmConst.QUEUE_TOPIC_USER);
    }

    @Bean
    public Queue queueQosMessge(){
        return  new Queue("qosQueue",true);
    }

    /*@Bean
    public Queue queueDelayMessge(){
        return new Queue("delayQueue",true);
    }*/

    //TODO 申明交换器(topic交换器)
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(RmConst.EXCHANGE_TOPIC);
    }

    @Bean
    public TopicExchange qosExchange(){
        return new TopicExchange("qosExchange",true,false);
    }

    /*@Bean
    public CustomExchange delayExchange(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange("delay_exchange", "x-delayed-message",true, false,args);
    }*/

    //TODO 绑定关系
    @Bean
    public Binding bindingEmailExchangeMessage() {
        return BindingBuilder
                .bind(queueEmailMessage())
                .to(exchange())
                .with("sb.*.email");
    }
    @Bean
    public Binding bindingUserExchangeMessages() {
        return BindingBuilder
                .bind(queueUserMessages())
                .to(exchange())
                .with("sb.*.user");
    }

    @Bean
    public Binding bindingQosExchangeMessages(){
        return BindingBuilder.
                bind(queueQosMessge()).
                to(qosExchange()).
                with("qos.#");
    }

    /*@Bean
    public Binding bindingDelayExchangeMessages(){
        return BindingBuilder
                .bind(queueDelayMessge())
                .to(delayExchange())
                .with("delayQueue").noargs();
    }*/

    //===============以上是验证topic Exchange==========

    //===============以下是验证Fanout Exchange==========
    //TODO 申明队列
    @Bean
    public Queue AMessage() {
        return new Queue("sb.fanout.A");
    }
    //TODO 申明交换器(fanout交换器)
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(RmConst.EXCHANGE_FANOUT);
    }
    //TODO 绑定关系
    @Bean
    Binding bindingExchangeA(Queue AMessage,FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(AMessage).to(fanoutExchange);
    }

    //===============以上是验证Fanout Exchange的交换器==========


    //===============以下是验证DLE Exchange==========
    @Bean("deadLetterExchange")
    public Exchange deadLetterExchange() {
        return ExchangeBuilder.directExchange("DL_EXCHANGE").durable(true).build();
    }
    @Bean("deadLetterQueue")
    public Queue deadLetterQueue() {
        Map<String, Object> args = new HashMap<>(2);
        args.put(RmConst.DEAD_LETTER_EXCHANGE_KEY, "DL_EXCHANGE");
        args.put(RmConst.DEAD_LETTER_ROUTING_KEY, "KEY_R");
        args.put(RmConst.X_MESSAGE_TTL, 20*1000);
        return QueueBuilder.durable("DL_QUEUE").withArguments(args).build();
    }

    /**
     * 定义死信队列转发队列.
     *
     * @return the queue
     */
    @Bean("redirectQueue")
    public Queue redirectQueue() {
        return QueueBuilder.durable("REDIRECT_QUEUE").build();
    }

    /**
     * 死信路由通过 DL_KEY 绑定键绑定到死信队列上.
     *
     * @return the binding
     */
    @Bean
    public Binding deadLetterBinding() {
        return new Binding("DL_QUEUE", Binding.DestinationType.QUEUE, "DL_EXCHANGE", "DL_KEY", null);

    }

    /**
     * 死信路由通过 KEY_R 绑定键绑定到死信队列上.
     *
     * @return the binding
     */
    @Bean
    public Binding redirectBinding() {
        return new Binding("REDIRECT_QUEUE", Binding.DestinationType.QUEUE, "DL_EXCHANGE", "KEY_R", null);
    }

    //===============以下是验证DLE Exchange的交换器==========


    //===============消费者确认==========
    @Bean
    public SimpleMessageListenerContainer messageContainer() {
        SimpleMessageListenerContainer container
                = new SimpleMessageListenerContainer(connectionFactory());
        //TODO 绑定了这个sb.user队列
        container.setQueues(userQueue());
        //TODO 手动提交
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        //TODO 消费确认方法
        container.setMessageListener(userReceiver);
        return container;
    }

    @Bean
    public SimpleMessageListenerContainer qosMessageContainer(){
        SimpleMessageListenerContainer container
                = new SimpleMessageListenerContainer(connectionFactory());
        container.setConcurrentConsumers(5);
        container.setMaxConcurrentConsumers(10);
        //预处理5
        container.setPrefetchCount(1);


        container.setQueues(queueQosMessge());
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener(qosReceiver);
        return container;
    }

    //===============生产者发送确认==========
    @Bean
    public RabbitTemplate.ConfirmCallback confirmCallback(){
        return new RabbitTemplate.ConfirmCallback(){

            @Override
            public void confirm(CorrelationData correlationData,
                                boolean ack, String cause) {
                if (ack) {
                    System.out.println("发送者确认发送给mq成功");
                } else {
                    //处理失败的消息
                    System.out.println("发送者发送给mq失败,考虑重发:"+cause);
                }
            }
        };
    }
    //===============失败通知==========
    @Bean
    public RabbitTemplate.ReturnCallback returnCallback(){
        return new RabbitTemplate.ReturnCallback(){

            @Override
            public void returnedMessage(Message message,
                                        int replyCode,
                                        String replyText,
                                        String exchange,
                                        String routingKey) {
                System.out.println("无法路由的消息，需要考虑另外处理。");
                System.out.println("Returned replyText："+replyText);
                System.out.println("Returned exchange："+exchange);
                System.out.println("Returned routingKey："+routingKey);
                String msgJson  = new String(message.getBody());
                System.out.println("Returned Message："+msgJson);
            }
        };
    }

}
