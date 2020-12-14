package com.baidu.rabbitmq.simple;

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
public class ReceiveACK {

    //队列名称
    private final static  String QUEUE_NAME = "simple_queue";

    //主函数
    public static void main(String[] args) throws Exception {

        // 获取连接
        Connection connection = RabbitmqConnectionUtil.getConnection();
        // 创建通道
        Channel channel = connection.createChannel();
        // 声明队列
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        // 定义队列
        DefaultConsumer consumer = new DefaultConsumer(channel){

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {

                //消息中参数信息
                String msg = new String(body);
                System.out.println("收到消息 执行中: "+ msg + "!" );
                 /*
                param1 : （唯一标识 ID）
                param2 : 是否进行批处理
                 */
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        };
        /*
       param1 : 队列名称
       param2 : 是否自动确认消息
       param3 : 消费者
        */
        channel.basicConsume(QUEUE_NAME,false,consumer);

        //消费者需要时刻监听消息，不需要关闭
    }

}
