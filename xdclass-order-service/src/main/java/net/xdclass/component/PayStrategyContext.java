package net.xdclass.component;

import net.xdclass.vo.PayInfoVO;

/**
 * @Description:
 * @author: tanshiwei
 * @date: 2022/1/28
 * @Version: 1.0
 */
public class PayStrategyContext {

    public PayStrategy payStrategy;

    public PayStrategyContext(PayStrategy payStrategy){
        this.payStrategy = payStrategy;
    }

    String executeUnifiedorder(PayInfoVO payInfoVO){
        return payStrategy.unifiedorder(payInfoVO);
    }

    public String executeQueryPaySuccess(PayInfoVO payInfoVO) {
        return payStrategy.queryPaySuccess(payInfoVO);
    }
}
