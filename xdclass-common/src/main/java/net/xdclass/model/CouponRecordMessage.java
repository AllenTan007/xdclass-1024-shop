package net.xdclass.model;

import lombok.Data;

import java.io.Serializable;


@Data
public class CouponRecordMessage{

    /**
     * 消息id
     */
    private String messageId;

    /**
     * 订单id
     */
    private String outTradeNo;

    /**
     * 库存锁定任务id
     */
    private Long taskId;
}
