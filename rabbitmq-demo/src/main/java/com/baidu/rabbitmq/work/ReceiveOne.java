package com.baidu.rabbitmq.work;

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
public class ReceiveOne {

    //队列名称
    private final static String QUEUE_NAME = "test_work_queue";


    public static void main(String[] args) throws Exception {

        Connection connection = RabbitmqConnectionUtil.getConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME,false,false,false,null);

        DefaultConsumer consumer = new DefaultConsumer(channel){

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                //消息中的参数
                String msg = new String(body);
                System.out.println("[消费者-1] 收到消息: "+msg);

                //1.唯一标识 2.是否要进行批处理
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        };

        /*
        * 队列名称
        * 是否自动确认消息
        * 消费者
        * */
        channel.basicConsume(QUEUE_NAME,false,consumer);
        //消费者需要时刻监听消息，不需要关闭通道和连接
    }


}
