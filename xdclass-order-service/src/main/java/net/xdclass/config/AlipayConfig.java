package net.xdclass.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群
 * @Version 1.0
 **/

public class AlipayConfig {

    /**
     * 支付宝网关地址  TODO
     */
    public static final String PAY_GATEWAY = "https://openapi.alipaydev.com/gateway.do";


    /**
     * 支付宝 APPID TODO
     */
    public static final String APPID = "2016092000555936";

    /**
     * 应用私钥 TODO
     */
    public static final String APP_PRI_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDh2PCkJ956QB49e3YK/RBAbAcTolDwKNa9qvmKq1LwZ74It+UDkf5YDkcQXAjsLri5A8B1j910ERIyv0UjIZUUu1gNry57vqA8gLHtvgthyRNh3897kqEXL8AxrEW83R/XjZVcVcqd6f2yOBW6N4lPj60qM90WTo/n2+I1wlXY5moRguB8NFoLC8Q1kAzQvjqILUIOjbqodoxcKRVS9RuAduNfE5XzmYFCDnB9RdtWXs10jfLG1pF37FY9RKOvMRNzlo5sH23HmDizHz29dUk9RRHge21hYgECP0hs8FHJBzPt6tIvbDE2Wu1uwQ3Bhrl4pURZ7d0CWr/cPRGaciEBAgMBAAECggEAL1QtKdmJEAl7zNqgpDDgRP/eg8jSOWH1jo3T+bFpkiPLeTxAa/0eVgm37r+6xvQLlgopPPnHKNmi/KuEq5YQDeYsz8FUdm4+Wi+GGhJnhDiFLU+fxX+27or9NeuqOagFUkDDejQoX+t3VO/X7cxRpDCx01RHErOoCKjVwPpWzzslWmVNdIwZFowFJosiHIj1u4Jt72dJ0AXeYoXUvaoKGEc0pb0TUlLXiosksWjYa/gZI7m4hxuzbUT8k0V/+zMxQ3fIJ3BNUPg0eH3HZRI/rZZnOinwxmX5BMRZDzuvm3TNxRO0Tz0EcQE+b3Fzcgnod9L96gAuJSYRjOJmMN2JIQKBgQDyvy/XxytWzwcHxGDq53VLOWzZr81dVz53AMx5y1qhcTXBC4v8s8Bp8yq3xw9v7I3EHVWuKRwRJn8JuTZKJCQONM6p09kHq8hZX3neDJtJg/+CB6hWKtwWGxJpV5GSzmmEE02HC29UZlz0WVOluVU3ETlR78chsKKE5EZkG0HlBQKBgQDuLY3myRAQt7q0RHFw4D5EjsIFZUCooptQgzWzgo5diN+9/ShdzKaKz/ByujXJ+hRX0V6WPknB4AOmp0i03ZD7bajFexOSj0a7CmL7DpRYrR30pA1N29hHh4b609GDwefjVuo2i2oPlkn1pCLnpmuxkV8XHeAL7jdN0Hw7IuOMzQKBgH7jAw4mlPfmdcVQmFyRqlUs6kILzCxbW1J3P2r2qiQzeiazc3QfPZfkPNMdoWse2qfFsbC82mf4mHUrtD4jEBnA7roE+7Av0iUtbBVuv4k5D0kX2Z1Y//wqIICh/n0fmjYopODPUF1suHAddUTuUKXdQfobfRqWKw2OCWFwggxFAoGBAN7Ig4PMrBdHE/+MSaPwTK4p7jfHxtw2BRshL/jx9KJu5gG7K6fZRipvaBSYMrnzDlY6Q8Q7DRiopiGbNbnfyb40i3n0rpEXLWzEwWLd90qe23c6gCtEqR/3F/3EZaRdmr6eTMOqUhG0XQfEeNW/Z9qXBKAF1My0DMzfZ2SVNM09AoGBAIvnGe74eaN+uXFUfDU8NC6lnZJDARU11SzHc3yuOVyl4Sr/dGEytWo4Y5F6XYEsWcO6b+VDMI5qMzXpVIAO51esiapR6R7grVadiExRP1xcx+0wqM3zjqQxH8NrUuxgTFXROzQaOSYOmcZy6BNEqLpyRO4GlXK4TIbvaw47hF8I";

    /**
     * 支付宝公钥 TODO
     */
    public static final String ALIPAY_PUB_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAk8OPeu/Bhbxr8chgRXnDSm0FDMZoK3r/qLLzqbv+L2/mYTLxHKw5U3c5XPHXar5SVWRp+IgLsZdTfYeZ+jYFmY+dTYk8mG8GXc1uYwHFADcWQAcR4KA3cbxiaubg7g//ECEhwA5CmaosjJ2p8UAz4j7cDB37TDON7oGMFbkXnSBlGhj5oy6rxdUXS+KeG9YmRPkQU3x3ljK37RRLFhwPPRR218IGDCmCtnI5ddKEq1hMAPnYmKNezerxSGGxxIaggt+sDwN1S3GNNSU7AelLPuvNEza49KaQijsBJpaLKUvqt5KO4IVfvPCmRsIFe7KzgdEmjygIY81qXacPzopJIQIDAQAB";


    /**
     * 签名类型
     */
    public static final String SIGN_TYPE = "RSA2";


    /**
     * 字符编码
     */
    public static final String CHARSET = "UTF-8";


    /**
     * 返回参数格式
     */
    public static final String FORMAT = "json";


    /**
     * 构造函数私有化
     */
    private AlipayConfig() {

    }


    public volatile static AlipayClient instance = null;

    public static AlipayClient getInstance() {

        if (instance == null) {
            synchronized (AlipayConfig.class) {
                if (instance == null) {
                    instance = new DefaultAlipayClient(PAY_GATEWAY, APPID, APP_PRI_KEY, FORMAT, CHARSET, ALIPAY_PUB_KEY, SIGN_TYPE);
                }
            }
        }
        return instance;
    }


}
