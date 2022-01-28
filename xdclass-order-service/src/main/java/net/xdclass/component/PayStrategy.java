package net.xdclass.component;

import net.xdclass.vo.PayInfoVO;

/**
 * @Description:
 * @author: tanshiwei
 * @date: 2022/1/28
 * @Version: 1.0
 */
public interface PayStrategy {

    /**
     * 下单
     * @return
     */
    String unifiedorder(PayInfoVO payInfoVO);

    String queryPaySuccess(PayInfoVO payInfoVO);
}
