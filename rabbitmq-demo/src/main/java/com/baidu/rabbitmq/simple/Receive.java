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
public class Receive {


    //队列名称
    private final static  String QUEUE_NAME = "simple_queue";


    //主函数
    public static void main(String[] args) throws Exception {


        //获取连接
        Connection connection = RabbitmqConnectionUtil.getConnection();
        //创建通道
        Channel channel = connection.createChannel();
        //声明队列
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        //定义队列
        DefaultConsumer consumer = new DefaultConsumer(channel){
            // 监听队列中的消息，如果有消息，进行处理
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println("-----------consume message----------");
                System.out.println("consumerTag: " + consumerTag);
                System.out.println("envelope: " + envelope);
                System.out.println("properties: " + properties);
                // body： 消息中参数信息
                String msg = new String(body);
                System.out.println(" 收到消息，执行中 : " + msg + "!");
            }

        };

        /*
        *  param1 : 队列名称
           param2 : 是否自动确认消息
           param3 : 消费者
        * */
        channel.basicConsume(QUEUE_NAME,true,consumer);

    }



}
