package net.xdclass.controller;


import com.google.code.kaptcha.Producer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.xdclass.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/user/v1")
@Slf4j
@Api("通知模块")
public class NotifyController {


    @Autowired
    private Producer captchaProducer;
    @Autowired
    private StringRedisTemplate redisTemplate;

    public static final long CAPTCHA_CODE_EXPIRED = 60*1000*10;

    @ApiOperation("获取图形验证码")
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {
        String captchaText = captchaProducer.createText();
        log.info("图形验证码:{}", captchaText);

        //存储
        redisTemplate.opsForValue().set(getCaptchaKey(request),captchaText,CAPTCHA_CODE_EXPIRED, TimeUnit.MILLISECONDS);
        BufferedImage captchaImage = captchaProducer.createImage(captchaText);
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            ImageIO.write(captchaImage, "jpg", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String getCaptchaKey(HttpServletRequest request){
        String ip = CommonUtil.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");
        String key = "user-service:captcha"+CommonUtil.MD5(ip+userAgent);

        log.info("ip={}",ip);
        log.info("userAgent={}",userAgent);
        log.info("key={}",key);

        return key;
    }
}
