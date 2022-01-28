package net.xdclass.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群
 * @Version 1.0
 **/

@Configuration
@Data
public class PayUrlConfig {

    /**
     * 支付成功页面跳转
     */
    @Value("${alipay.success_return_url}")
    private String alipaySuccessReturnUrl;


    /**
     * 支付成功，回调通知
     */
    @Value("${alipay.callback_url}")
    private String alipayCallbackUrl;
}
