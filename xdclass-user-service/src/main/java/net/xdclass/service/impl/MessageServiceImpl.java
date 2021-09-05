package net.xdclass.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.xdclass.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


/**
 * 发送邮件
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Value("${spring.mail.from}")
    private String from;
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendMessage(String to, String subject, String text) {


        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setText(text);
        simpleMailMessage.setSubject(subject);
        mailSender.send(simpleMailMessage);
        log.info("邮件发送信息:{}", simpleMailMessage);
    }
}
