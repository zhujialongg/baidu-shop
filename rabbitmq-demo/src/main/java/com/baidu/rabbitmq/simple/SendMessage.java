package com.baidu.rabbitmq.simple;

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


    //序列名称
    private final static  String QUEUE_NAME = "simple_queue";
    //主函数
    public static void main(String[] args) throws Exception {

        //获取连接
        Connection connection = RabbitmqConnectionUtil.getConnection();
        //获取通道
        Channel channel = connection.createChannel();
           /*
        param1:队列名称
        param2: 是否持久化
        param3: 是否排外
        param4: 是否自动删除
        param5: 其他参数
         */
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        //编写消息的内容
        String message = "good good study";
        /*
        param1: 交换机名称
        param2: routingKey
        param3: 一些配置信息
        param4: 发送的消息
         */
        //发送消息
        channel.basicPublish("",QUEUE_NAME,null,message.getBytes());

        System.out.println(" 消息发送 '" + message + "' 到队列 success");
        //关闭连接 和通道
        channel.close();
        connection.close();
    }

}
