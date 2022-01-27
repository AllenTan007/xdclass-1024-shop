package net.xdclass.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import net.xdclass.model.OrderMessage;
import net.xdclass.service.ProductOrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Description:
 * @author: tanshiwei
 * @date: 2022/1/26
 * @Version: 1.0
 */

@Component
@RabbitListener
@Slf4j
public class ProductOrderMQListener {

    @Autowired
    private ProductOrderService productOrderService;



    @RabbitHandler
    public void closeProductOrder(OrderMessage orderMessage, Message message, Channel channel) throws IOException {
        log.info("监听到消息：closeProductOrder:{}",orderMessage);
        long msgTag = message.getMessageProperties().getDeliveryTag();

        boolean flag = productOrderService.closeProductOrder(orderMessage);

        try {
            if (flag){
                channel.basicAck(msgTag,false);
            }else {

                channel.basicReject(msgTag,true);
            }
        } catch (IOException e) {
            log.error("定时关单失败:",orderMessage);
            channel.basicReject(msgTag,true);
        }

    }
}
