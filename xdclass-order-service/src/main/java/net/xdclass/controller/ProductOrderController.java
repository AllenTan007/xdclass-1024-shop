package net.xdclass.controller;


import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import net.xdclass.enums.BizCodeEnum;
import net.xdclass.enums.ClientType;
import net.xdclass.enums.ProductOrderPayTypeEnum;
import net.xdclass.request.ConfirmOrderRequest;
import net.xdclass.request.RepayOrderRequest;
import net.xdclass.service.ProductOrderService;
import net.xdclass.util.CommonUtil;
import net.xdclass.util.JsonData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author tanshiwei
 * @since 2022-01-14
 */
@Api("订单模块")
@RestController
@RequestMapping("/api/order/v1")
@Slf4j
public class ProductOrderController {

    @Autowired
    private ProductOrderService orderService;
    @Autowired
    private ProductOrderService productOrderService;

    @ApiOperation("提交订单")
    @PostMapping("confirm")
    public void confirmOrder(@ApiParam("订单对象") @RequestBody ConfirmOrderRequest orderRequest, HttpServletResponse response) {
        JsonData jsonData = productOrderService.confirmOrder(orderRequest);
        if (jsonData.getCode() == 0) {

            String clientType = orderRequest.getClientType();
            String payType = orderRequest.getPayType();
            if (payType.equalsIgnoreCase(ProductOrderPayTypeEnum.ALIPAY.name())) {
                log.info("创建支付宝订单成功:{}", orderRequest.toString());

                if (clientType.equalsIgnoreCase(ClientType.H5.name())) {
                    writeData(response, jsonData);
                } else if (clientType.equalsIgnoreCase(ClientType.APP.name())) {
                    //APP SDK支付  TODO
                }
            } else if (payType.equalsIgnoreCase(ProductOrderPayTypeEnum.WECHAT.name())) {
                //todo 微信支付
            }
        } else {
            log.error("创建订单失败:{}", jsonData.getData());
        }
    }


    @ApiOperation("重新支付订单")
    @PostMapping
    public void repay(@ApiParam("订单对象") @RequestBody RepayOrderRequest repayOrderRequest, HttpServletResponse response){

        JsonData jsonData = orderService.repay(repayOrderRequest);
        if(jsonData.getCode() == 0){

            String client = repayOrderRequest.getClientType();
            String payType = repayOrderRequest.getPayType();

            //如果是支付宝网页支付，都是跳转网页，APP除外
            if(payType.equalsIgnoreCase(ProductOrderPayTypeEnum.ALIPAY.name())){

                log.info("重新支付订单成功:{}",repayOrderRequest.toString());

                if(client.equalsIgnoreCase(ClientType.H5.name())){
                    writeData(response,jsonData);

                }else if(client.equalsIgnoreCase(ClientType.APP.name())){
                    //APP SDK支付  TODO
                }

            } else if(payType.equalsIgnoreCase(ProductOrderPayTypeEnum.WECHAT.name())){

                //微信支付 TODO
            }

        } else {
            log.error("重新支付订单失败{}",jsonData.toString());
            CommonUtil.sendJsonMessage(response,jsonData);
        }
    }

    @ApiOperation("查询订单状态")
    @GetMapping("/query_state")
    public JsonData queryProductOrderState(@RequestParam("out_trade_no") String outTradeNo) {
        String state = orderService.queryProductOrderState(outTradeNo);
        return StringUtils.isBlank(state) ? JsonData.buildResult(BizCodeEnum.ORDER_CONFIRM_NOT_EXIST) : JsonData.buildSuccess(state);
    }

    private void writeData(HttpServletResponse response, JsonData jsonData) {
        try {
            response.setContentType("text/html;charset=UTF8");
            response.getWriter().write(JSON.toJSONString(jsonData.getData()));
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            log.error("写出HTML异常:{}", e);
        }
    }
}

