package net.xdclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.xdclass.mapper.ProductOrderMapper;
import net.xdclass.model.ProductOrderDO;
import net.xdclass.request.ConfirmOrderRequest;
import net.xdclass.service.ProductOrderService;
import net.xdclass.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 订单提交
 *
 * @author tanshiwei
 * @since 2022-01-14
 */
@Service
@Slf4j
public class ProductOrderServiceImpl implements ProductOrderService {


    @Autowired
    private ProductOrderMapper productOrderMapper;

    /**
     * * 订单防重校验
     * * 校验客户地址
     * * 查询商品价格信息
     * * 校验订单价格
     * * 获取优惠券
     * * 锁定优惠券
     * * 锁定商品库存
     * * 创建订单对象
     * * 创建订单子对象
     * * 发送延迟消息--用于自动关单
     * * 创建支付信息-对接三方支付
     * @param orderRequest
     * @return
     */
    @Override
    public JsonData confirmOrder(ConfirmOrderRequest orderRequest) {

        return null;
    }

    @Override
    public String queryProductOrderState(String outTradeNo) {

        ProductOrderDO productOrderDO = productOrderMapper.selectOne(new QueryWrapper<ProductOrderDO>().eq("out_trade_no", outTradeNo));

        if (productOrderDO == null){
            return "";
        }else {
            return productOrderDO.getState();
        }
    }
}
