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
        String code = CommonUtil.getRandomCode(6);
        String value = code+"_"+System.currentTimeMillis();
        redisTemplate.opsForValue().set(cacheKey,value,1000*60, TimeUnit.MILLISECONDS);
        if (CheckUtil.isEmail(to)) {
            //邮箱验证码
            messageService.sendMessage(to, SUBJECT, code);
            return JsonData.buildSuccess();
        } else if (CheckUtil.isPhone(to)) {
            //短信验证码
        }
        return JsonData.buildResult(BizCodeEnum.CODE_TO_ERROR);
    }

    @Override
    public boolean checkCode(SendCodeEnum userRegister, String mail, String code) {
        String cacheKey = String.format(CacheKey.CHECK_CODE_KEY,userRegister.name(),mail);
        String cacheCode = redisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.isNotBlank(cacheCode)){
            String cacheValue = cacheCode.split("_")[0];
            if (cacheValue.equals(code)){
                redisTemplate.delete(cacheKey);
                return true;
            }
        }
        return false;
    }
}
