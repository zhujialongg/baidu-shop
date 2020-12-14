package com.baidu.rabbitmq.confirm;

import com.baidu.rabbitmq.utils.RabbitmqConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
public class SendMessage {

    //交换机名称
    private final static String EXCHANGE_NAME = "exchange_pub";

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitmqConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

        //确认模式
        channel.confirmSelect();
        //声明交换机，指定类型为fanout
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");

        //编写消息内容
        String message =  "more and more";

        channel.basicPublish(EXCHANGE_NAME,"save",
                MessageProperties.PERSISTENT_BASIC,message.getBytes());

        //确认消息，发送完毕
        if(channel.waitForConfirms()){
            System.out.println("发送成功");
        }else {
            System.out.println("发送失败");
        }
        //关闭连接 释放资源
        channel.close();
        connection.close();
    }



}
