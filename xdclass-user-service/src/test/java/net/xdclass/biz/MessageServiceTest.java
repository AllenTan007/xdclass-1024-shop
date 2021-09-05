package net.xdclass.biz;


import net.xdclass.UserApplication;
import net.xdclass.service.MessageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = UserApplication.class)
@RunWith(SpringRunner.class)
public class MessageServiceTest {


    @Autowired
    private MessageService messageService;

    @Test
    public void testAddress(){

        String to = "853212308@qq.com";
        String subject = "你好";
        String text = "hello.world";

        messageService.sendMessage(to,subject,text);
    }
}
