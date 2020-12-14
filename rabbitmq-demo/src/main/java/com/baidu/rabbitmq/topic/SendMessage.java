package com.baidu.rabbitmq.topic;

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
    private final static String EXCHANGE_NAME = "topic_exchange_test";


    public static void main(String[] args) throws Exception {
        //获取连接
        Connection connection = RabbitmqConnectionUtil.getConnection();
        //创建通道
        Channel channel = connection.createChannel();
        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME,"topic",true);
        //编写消息内容
        String message = "商品删除成功  id ： 153";
        //发送消息
        //channel.basicPublish(EXCHANGE_NAME, "goods.save", null, message.getBytes());
        //channel.basicPublish(EXCHANGE_NAME, "goods.update", null, message.getBytes());
        channel.basicPublish(EXCHANGE_NAME, "goods.delete",MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());

        System.out.println(" [商品服务] 发送消息routingKey ：delete   '" + message );

        //关闭通道，连接
        channel.close();
        connection.close();
    }

}
