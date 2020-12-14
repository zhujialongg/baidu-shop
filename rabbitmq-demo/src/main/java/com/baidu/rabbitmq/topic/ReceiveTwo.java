package com.baidu.rabbitmq.topic;

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

    //交换机名
    private final static String EXCHANGE_NAME = "topic_exchange_test";

    //队列名称
    private final static String QUEUE_NAME = "topic_exchange_queue_2";

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitmqConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);

        //消息队列绑定到交换机
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"goods.update");
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"goods.save");
        //定义队列
        DefaultConsumer consumer = new DefaultConsumer(channel){
            // 监听队列中的消息，如果有消息，进行处理
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                // body： 消息中参数信息
                String msg = new String(body);
                System.out.println(" [消费者2模拟es服务] 接收到消息 : " + msg );
            }
        };

        /*
         * 队列名称
         * 是否自动确认消息
         * 消费者
         * */
        channel.basicConsume(QUEUE_NAME,true,consumer);
    }


}
