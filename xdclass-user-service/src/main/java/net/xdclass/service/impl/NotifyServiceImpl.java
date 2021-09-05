package net.xdclass.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.select.KSQLJoinWindow;
import net.xdclass.constant.CacheKey;
import net.xdclass.enums.BizCodeEnum;
import net.xdclass.enums.SendCodeEnum;
import net.xdclass.service.MessageService;
import net.xdclass.service.NotifyService;
import net.xdclass.util.CheckUtil;
import net.xdclass.util.CommonUtil;
import net.xdclass.util.JsonData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class NotifyServiceImpl implements NotifyService {

    /**
     * 验证码的标题
     */
    private static final String SUBJECT = "威震天你好";

    /**
     * 验证码的内容
     */
    private static final String CONTENT = "码:%s";

    @Autowired
    private MessageService messageService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public JsonData sendCode(SendCodeEnum sendCodeType, String to) {

        String cacheKey = String.format(CacheKey.CHECK_CODE_KEY,sendCodeType.name(),to);

        String cacheCode = redisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.isNotBlank(cacheCode)){
            long time = Long.parseLong(cacheCode.split("_")[1]);
            if(System.currentTimeMillis() - time < 1000*60){
                return JsonData.buildResult(BizCodeEnum.CODE_LIMITED);
            }
        }

        if (CheckUtil.isEmail(to)) {
            //邮箱验证码
            String code = String.format(CONTENT, CommonUtil.getRandomCode(6));
            String cacheValue = code+"_"+System.currentTimeMillis();
            redisTemplate.opsForValue().set(cacheKey,cacheValue,1000*60, TimeUnit.MILLISECONDS);
            messageService.sendMessage(to, SUBJECT, code);
            return JsonData.buildSuccess();
        } else if (CheckUtil.isPhone(to)) {
            //短信验证码
        }
        return JsonData.buildResult(BizCodeEnum.CODE_TO_ERROR);
    }
}
