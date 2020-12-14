package com.baidu.rabbitmq.direct;

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
    private final static String EXCHANGE_NAME = "direct_exchange_test";

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitmqConnectionUtil.getConnection();

        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME,"direct");

        //编写消息内容
       // String message = "商品新增成功  id ： 153";
       // String message = "商品修改成功  id ： 153";
        String message = "商品删除成功  id ： 153";

        //发送消息
//        channel.basicPublish(EXCHANGE_NAME,"save",null,message.getBytes());
//        channel.basicPublish(EXCHANGE_NAME,"update",null,message.getBytes());
       channel.basicPublish(EXCHANGE_NAME,"delete",null,message.getBytes());
        System.out.println(" [商品服务] 发送消息routingKey ：save   '" + message );

        //关闭通道和连接
        channel.close();
        connection.close();

    }

}
