package net.xdclass.service;

import net.xdclass.model.OrderMessage;
import net.xdclass.request.ConfirmOrderRequest;
import net.xdclass.request.RepayOrderRequest;
import net.xdclass.util.JsonData;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author tanshiwei
 * @since 2022-01-14
 */
public interface ProductOrderService {

    JsonData confirmOrder(ConfirmOrderRequest orderRequest);

    String queryProductOrderState(String outTradeNo);

    boolean closeProductOrder(OrderMessage orderMessage);

    JsonData repay(RepayOrderRequest repayOrderRequest);

}
