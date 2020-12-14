package com.baidu.rabbitmq.fanout;

import com.baidu.rabbitmq.utils.RabbitmqConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
public class SendMessage {

    //交换机名称
    private final static String EXCHANGE_NAME ="fanout_exchange_test";

    //主函数
    public static void main(String[] args) throws Exception {

        Connection connection = RabbitmqConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

         /*
        param1: 交换机名称
        param2: 交换机类型  fanout 扇形交换机
         */
        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");

        //消息内容
        String message  = "good good study";
        //发送消息
        channel.basicPublish(EXCHANGE_NAME,"",null,message.getBytes());

        System.out.println("消息发送 "+message + "到交换机 成功");

        //关闭通道和连接
        channel.close();
        connection.close();
    }


}
