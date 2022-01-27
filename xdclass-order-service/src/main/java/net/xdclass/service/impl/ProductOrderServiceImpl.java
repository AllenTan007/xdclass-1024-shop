package net.xdclass.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.xdclass.config.RabbitMQConfig;
import net.xdclass.enums.BizCodeEnum;
import net.xdclass.enums.CouponStateEnum;
import net.xdclass.enums.ProductOrderStateEnum;
import net.xdclass.enums.ProductOrderTypeEnum;
import net.xdclass.exception.BizException;
import net.xdclass.feign.CouponFeignSerivce;
import net.xdclass.feign.ProductFeignService;
import net.xdclass.feign.UserFeignService;
import net.xdclass.interceptor.LoginInterceptor;
import net.xdclass.mapper.ProductOrderItemMapper;
import net.xdclass.mapper.ProductOrderMapper;
import net.xdclass.model.LoginUser;
import net.xdclass.model.OrderMessage;
import net.xdclass.model.ProductOrderDO;
import net.xdclass.model.ProductOrderItemDO;
import net.xdclass.request.ConfirmOrderRequest;
import net.xdclass.request.LockCouponRecordRequest;
import net.xdclass.request.OrderItemRequest;
import net.xdclass.service.ProductOrderService;
import net.xdclass.util.CommonUtil;
import net.xdclass.util.JsonData;
import net.xdclass.vo.CouponRecordVO;
import net.xdclass.vo.LockProductRequest;
import net.xdclass.vo.OrderItemVO;
import net.xdclass.vo.ProductOrderAddressVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private ProductOrderItemMapper productOrderItemMapper;
    @Autowired
    private UserFeignService userFeignService;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private CouponFeignSerivce couponFeignSerivce;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitMQConfig rabbitMQConfig;

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
     *
     * @param orderRequest
     * @return
     */
    @Override
    public JsonData confirmOrder(ConfirmOrderRequest orderRequest) {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        String orderOutTradeNo = CommonUtil.getStringNumRandom(32);


        //获取收货地址详情
        ProductOrderAddressVO addressVO = this.getUserAddress(orderRequest.getAddressId());
        log.info("收货地址信息:{}", addressVO);

        //查询商品定价信息
        List<Long> productIdList = orderRequest.getProductIdList();

        JsonData cartItemDate = productFeignService.confirmOrderCartItem(productIdList);

        List<OrderItemVO> orderItemList = cartItemDate.getData(new TypeReference<List<OrderItemVO>>() {
        });
        log.info("获取的商品:{}", orderItemList);
        if (orderItemList == null) {
            //购物车商品不存在
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_CART_ITEM_NOT_EXIST);
        }

        //验证价格，减去商品优惠券
        this.checkPrice(orderItemList, orderRequest);

        // 锁定优惠券
        this.lockCoupon(orderOutTradeNo, orderRequest.getCouponRecordId());

        // 库存锁定
        this.lockProductStock(orderItemList, orderRequest.getCouponRecordId());

        //创建订单
        ProductOrderDO productOrderDO = this.createOrder(loginUser, addressVO, orderOutTradeNo, orderRequest);

        //创建订单项
        this.saveProductOrderItems(orderOutTradeNo, productOrderDO.getId(), orderItemList);

        // 发送延迟消息,用于关单

        OrderMessage orderMessage = new OrderMessage();
        orderMessage.setOutTradeNo(orderOutTradeNo);

        rabbitTemplate.convertAndSend(rabbitMQConfig.getEventExchange(),rabbitMQConfig.getOrderCloseDelayRoutingKey(),orderMessage);


        //创建支付  TODO


        return null;
    }

    // 创建订单项
    private void saveProductOrderItems(String orderOutTradeNo, Long id, List<OrderItemVO> orderItemList) {
        List<ProductOrderItemDO> productOrderItemDOList = orderItemList.stream().map(obj -> {
            ProductOrderItemDO itemDO = new ProductOrderItemDO();
            itemDO.setBuyNum(obj.getBuyNum());
            itemDO.setProductId(obj.getProductId());
            itemDO.setProductImg(obj.getProductImg());
            itemDO.setProductName(obj.getProductTitle());

            itemDO.setOutTradeNo(orderOutTradeNo);
            itemDO.setCreateTime(new Date());

            //单价
            itemDO.setAmount(obj.getAmount());
            //总价
            itemDO.setTotalAmount(obj.getTotalAmount());
            itemDO.setProductOrderId(id);
            return itemDO;
        }).collect(Collectors.toList());

        productOrderItemMapper.insertBatch(productOrderItemDOList);
    }


    /**
     * 创建订单
     *
     * @param loginUser
     * @param addressVO
     * @param orderOutTradeNo
     * @param orderRequest
     * @return
     */
    private ProductOrderDO createOrder(LoginUser loginUser, ProductOrderAddressVO addressVO, String orderOutTradeNo, ConfirmOrderRequest orderRequest) {

        ProductOrderDO productOrderDO = new ProductOrderDO();

        productOrderDO.setOutTradeNo(orderOutTradeNo);
        productOrderDO.setState(ProductOrderStateEnum.NEW.name());
        productOrderDO.setCreateTime(new Date());
        productOrderDO.setTotalAmount(orderRequest.getTotalAmount());
        productOrderDO.setPayAmount(orderRequest.getRealPayAmount());
        productOrderDO.setNickname(loginUser.getName());
        productOrderDO.setHeadImg(loginUser.getHeadImg());
        productOrderDO.setUserId(loginUser.getId());
        productOrderDO.setDel(0);
        productOrderDO.setOrderType(ProductOrderTypeEnum.DAILY.name());
        productOrderDO.setReceiverAddress(JSON.toJSONString(addressVO));
        productOrderMapper.insert(productOrderDO);
        return productOrderDO;
    }

    /**
     * 锁定库存
     *
     * @param orderItemList
     * @param couponRecordId
     */
    private void lockProductStock(List<OrderItemVO> orderItemList, Long couponRecordId) {
        LockProductRequest lockProductRequest = new LockProductRequest();
        List<OrderItemRequest> orderItemRequestList = orderItemList.stream().map(obj -> {
            OrderItemRequest orderItemRequest = new OrderItemRequest();
            orderItemRequest.setBuyNum(obj.getBuyNum());
            orderItemRequest.setProductId(obj.getProductId());
            return orderItemRequest;
        }).collect(Collectors.toList());
        lockProductRequest.setOrderItemList(orderItemRequestList);
        lockProductRequest.setOrderOutTradeNo(couponRecordId.toString());
        JsonData jsonData = productFeignService.lockProductStock(lockProductRequest);
        if (jsonData.getCode() != 0) {
            log.error("锁定商品库存失败：{}", lockProductRequest);
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_LOCK_PRODUCT_FAIL);
        }

    }

    /**
     * 锁定优惠券状态
     *
     * @param orderOutTradeNo
     * @param couponRecordId
     */
    private void lockCoupon(String orderOutTradeNo, Long couponRecordId) {
        LockCouponRecordRequest lockCouponRecordRequest = new LockCouponRecordRequest();
        List<Long> lockCouponRecordIds = new ArrayList<>();
        lockCouponRecordIds.add(couponRecordId);
        lockCouponRecordRequest.setLockCouponRecordIds(lockCouponRecordIds);
        lockCouponRecordRequest.setOrderOutTradeNo(orderOutTradeNo);
        JsonData jsonData = couponFeignSerivce.lockCouponRecords(lockCouponRecordRequest);
        if (jsonData.getCode() != 0) {
            throw new BizException(BizCodeEnum.COUPON_RECORD_LOCK_FAIL);
        }
    }

    /**
     * 校验商品价格,使用优惠券
     *
     * @param orderItemList
     * @param orderRequest
     */
    private void checkPrice(List<OrderItemVO> orderItemList, ConfirmOrderRequest orderRequest) {

        BigDecimal totalAmount = new BigDecimal(0);
        if (orderItemList != null) {
            for (OrderItemVO orderItemVO : orderItemList) {
                totalAmount = totalAmount.add(orderItemVO.getTotalAmount());
            }
        }

        CouponRecordVO couponRecordVO = getCartCouponRecord(orderRequest.getCouponRecordId());
        if (couponRecordVO != null) {
            if (totalAmount.compareTo(couponRecordVO.getConditionPrice()) < 0) {
                throw new BizException(BizCodeEnum.ORDER_CONFIRM_COUPON_FAIL);
            }
            if (couponRecordVO.getPrice().compareTo(totalAmount) > 0) {
                totalAmount = BigDecimal.ZERO;
            } else {
                totalAmount = totalAmount.subtract(couponRecordVO.getPrice());
            }

            if (totalAmount.compareTo(orderRequest.getRealPayAmount()) != 0) {
                log.error("订单验价失败：{}", orderRequest);
                throw new BizException(BizCodeEnum.ORDER_CONFIRM_PRICE_FAIL);
            }
        }

    }

    private CouponRecordVO getCartCouponRecord(Long couponRecordId) {
        if (couponRecordId == null || couponRecordId < 0) {
            return null;
        }
        JsonData result = couponFeignSerivce.findUserCouponRecordById(couponRecordId);
        if (result.getCode() != 0) {
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_COUPON_FAIL);
        }
        if (result.getCode() == 0) {
            CouponRecordVO couponRecordVO = result.getData(new TypeReference<CouponRecordVO>() {
            });
            if (!couponAvailable(couponRecordVO)) {
                log.error("优惠券使用失败");
                throw new BizException(BizCodeEnum.COUPON_UNAVAILABLE);
            }
            return couponRecordVO;
        }
        return null;
    }

    private boolean couponAvailable(CouponRecordVO couponRecordVO) {
        if (couponRecordVO.getUseState().equalsIgnoreCase(CouponStateEnum.NEW.name())) {
            long currentTimestamp = CommonUtil.getCurrentTimestamp();
            long end = couponRecordVO.getEndTime().getTime();
            long start = couponRecordVO.getStartTime().getTime();
            return currentTimestamp >= start && currentTimestamp <= end;
        }
        return false;
    }


    private ProductOrderAddressVO getUserAddress(long addressId) {

        JsonData addressData = userFeignService.detail(addressId);

        if (addressData.getCode() != 0) {
            log.error("获取收获地址失败,msg:{}", addressData);
            throw new BizException(BizCodeEnum.ADDRESS_NO_EXITS);
        }

        ProductOrderAddressVO addressVO = addressData.getData(new TypeReference<ProductOrderAddressVO>() {
        });

        return addressVO;
    }

    @Override
    public String queryProductOrderState(String outTradeNo) {

        ProductOrderDO productOrderDO = productOrderMapper.selectOne(new QueryWrapper<ProductOrderDO>().eq("out_trade_no", outTradeNo));
        if (productOrderDO == null) {
            return "";
        } else {
            return productOrderDO.getState();
        }
    }

    @Override
    public boolean closeProductOrder(OrderMessage orderMessage) {

        ProductOrderDO productOrderDO = productOrderMapper.selectOne(new QueryWrapper<ProductOrderDO>().eq("out_trade_no", orderMessage.getOutTradeNo()));
        if (productOrderDO == null){
            //订单不存在
            log.warn("直接确认消息，订单不存在:{}",orderMessage);
            return true;
        }

        if (ProductOrderStateEnum.PAY.name().equalsIgnoreCase(productOrderDO.getState())){
            log.info("直接确认消息,订单已经支付:{}",orderMessage);
            return true;
        }

        //向第三方支付查询订单是否真的未支付  TODO


        String payResult = "";

        if (StringUtils.isBlank(payResult)){
            log.info("结果为空，则未支付成功，本地取消订单:{}",orderMessage);
            productOrderMapper.updateOrderPayState(ProductOrderStateEnum.CANCEL.name(), orderMessage.getOutTradeNo());
            return true;
        }else {
            log.info("结果为空，则未支付成功，本地取消订单:{}",orderMessage);
            productOrderMapper.updateOrderPayState(ProductOrderStateEnum.PAY.name(), orderMessage.getOutTradeNo());
            return true;
        }

    }
}
