package net.xdclass.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import net.xdclass.model.CouponRecordMessage;
import net.xdclass.service.CouponRecordService;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群
 * @Version 1.0
 **/

@Slf4j
@Component
@RabbitListener(queues = "${mqconfig.coupon_release_queue}")
public class CouponMQListener {


    @Autowired
    private CouponRecordService couponRecordService;

    @Autowired
    private RedissonClient redissonClient;

    /**
     *
     * 重复消费-幂等性
     *
     * 消费失败，重新入队后最大重试次数：
     *  如果消费失败，不重新入队，可以记录日志，然后插到数据库人工排查
     *
     *  消费者这块还有啥问题，大家可以先想下，然后给出解决方案
     *
     * @param recordMessage
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitHandler
    public void releaseCouponRecord(CouponRecordMessage recordMessage, Message message, Channel channel) throws IOException {

        log.info("监听到消息：releaseCouponRecord消息内容：{}", recordMessage);
        long msgTag = message.getMessageProperties().getDeliveryTag();

        boolean flag = couponRecordService.releaseCouponRecord(recordMessage);

        try {
            if (flag){
                channel.basicAck(msgTag,false);
            }else {
                channel.basicReject(msgTag,true);
                log.error("释放优惠券失败 flag=false,{}",recordMessage);
            }
        } catch (IOException e) {
            log.error("释放优惠券记录异常:{},msg:{}",e,recordMessage);
            channel.basicReject(msgTag,true);
        }


    }


}
