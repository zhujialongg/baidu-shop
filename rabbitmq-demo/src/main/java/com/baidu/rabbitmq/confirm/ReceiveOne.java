package com.baidu.rabbitmq.confirm;

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

    //交换机名称
    private final static  String EXCHANGE_NAME = "exchange_pub";
    //队列名称
    private final static String QUEUE_NAME ="fanout_exchange_queue_1";

    //主函数
    public static void main(String[] args) throws Exception {
        //获取连接
        Connection connection = RabbitmqConnectionUtil.getConnection();
        //创建通道
        Channel channel = connection.createChannel();
        //声明队列            队列名，是否持久化，是否排外，是否自动删除，其它参数
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        //绑定队列到交换机  最后一个参数是routingkey
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"");
        //定义队列
        DefaultConsumer consumer = new DefaultConsumer(channel){
            @Override   //重写是因为 此方法为抽象方法  。。。
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                //消息体  byte类型转String类型
                String message = new String(body);
                System.out.println("消费者one收到消息 : " + message );
            }
        };
        // 队列名称 是否自动确认消息 消费者
        channel.basicConsume(QUEUE_NAME,true,consumer);


    }



}
