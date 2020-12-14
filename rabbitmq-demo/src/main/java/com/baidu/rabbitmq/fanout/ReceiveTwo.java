package com.baidu.rabbitmq.fanout;

import com.baidu.rabbitmq.utils.RabbitmqConnectionUtil;
import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
public class ReceiveTwo {



    //交换机名称
    private final static  String EXCHANGE_NAME = "fanout_exchange_test";
    //队列名称
    private final static String QUEUE_NAME ="fanout_exchange_queue_2";

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitmqConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
//        param1:队列名称
//        param2: 是否持久化
//        param3: 是否排外
//        param4: 是否自动删除
//        param5: 其他参数
        //声明队列
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);

        //消息队列绑定到交换机  最后一个参数是routingKey
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"");

        //定义队列
        DefaultConsumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                // body： 消息中参数信息
                String msg = new String(body);
                System.out.println(" [消费者-2] 收到消息 : " + msg );
            }
        };

        /*
         * param1：队列名称
         * param2：是否自动确认消息
         * param3：消费者
         * */
        channel.basicConsume(QUEUE_NAME,true,consumer);

    }


}
