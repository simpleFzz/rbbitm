package cn.fzz;
/**

 *类说明：
 */
public class RmConst {

    public final static String QUEUE_HELLO = "sb.hello";
    public final static String QUEUE_USER = "sb.user";

    public final static String QUEUE_TOPIC_EMAIL = "sb.info.email";
    public final static String QUEUE_TOPIC_USER = "sb.info.user";
    public final static String RK_EMAIL = "sb.info.email";
    public final static String RK_USER = "sb.info.user";

    public final static String EXCHANGE_TOPIC = "sb.exchange";
    public final static String EXCHANGE_FANOUT = "sb.fanout.exchange";

    public static final String DEAD_LETTER_EXCHANGE_KEY = "x-dead-letter-exchange";
    public static final String DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";
    public static final String X_MESSAGE_TTL = "x-message-ttl";
}
