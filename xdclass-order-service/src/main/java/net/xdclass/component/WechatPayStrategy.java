package net.xdclass.component;

import lombok.extern.slf4j.Slf4j;
import net.xdclass.vo.PayInfoVO;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @author: tanshiwei
 * @date: 2022/1/28
 * @Version: 1.0
 */

@Slf4j
@Service
public class WechatPayStrategy implements PayStrategy {
    @Override
    public String unifiedorder(PayInfoVO payInfoVO) {
        return null;
    }

    @Override
    public String queryPaySuccess(PayInfoVO payInfoVO) {
        return null;
    }
}
