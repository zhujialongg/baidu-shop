package com.baidu.listener;

import com.baidu.constant.MqMessageConstant;
import com.baidu.service.ShopElasticsearchService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@Component
@Slf4j
public class GoodsListener {


    @Autowired
    private ShopElasticsearchService shopElasticsearchService;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            value = MqMessageConstant.SPU_QUEUE_SEARCH_SAVE,
                            durable = "true"
                    ),
                    exchange = @Exchange(
                            value = MqMessageConstant.EXCHANGE,
                            ignoreDeclarationExceptions = "true",
                            type = ExchangeTypes.TOPIC
                    ),
                    key = {MqMessageConstant.SPU_ROUT_KEY_SAVE,MqMessageConstant.SPU_ROUT_KEY_UPDATE}
            )
    )
    public void save(Message message, Channel channel) throws IOException {

        log.info("es服务接受到需要保存数据的消息: " + new String(message.getBody()));
        //新增数据到es
        shopElasticsearchService.saveData(Integer.parseInt(new String(message.getBody())));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
    }



    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            value = MqMessageConstant.SPU_QUEUE_SEARCH_DELETE,
                            durable = "true"
                    ),
                    exchange = @Exchange(
                            value = MqMessageConstant.EXCHANGE,
                            ignoreDeclarationExceptions = "true",
                            type = ExchangeTypes.TOPIC
                    ),
                    key = MqMessageConstant.SPU_ROUT_KEY_DELETE
            )
    )
    public void delete(Message message, Channel channel) throws IOException {

        log.info("es服务接受到需要删除数据的消息: " + new String(message.getBody()));
        //删除es数据
        shopElasticsearchService.delData(Integer.parseInt(new String(message.getBody())));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
    }



}
