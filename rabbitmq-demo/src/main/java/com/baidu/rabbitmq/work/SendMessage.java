package com.baidu.rabbitmq.work;

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
    private final static  String QUEUE_NAME= "test_work_queue";

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitmqConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);

        //循环发送消息100条
        for(int i=0;i<100;i++){
            //消息参数内容
            String message = "task - good study - " + i ;

            /*
            * 交换机名称
            * routingKey
            * 一些配置信息
            * 发送的消息
            * */
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
            System.out.println(" send '" + message + "' success");
        }

        //关闭通道和连接
        channel.close();
        connection.close();
    }


}
