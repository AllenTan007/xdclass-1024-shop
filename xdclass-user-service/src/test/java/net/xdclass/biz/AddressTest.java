package net.xdclass.biz;


import net.xdclass.UserApplication;
import net.xdclass.model.AddressDO;
import net.xdclass.service.AddressService;
import net.xdclass.vo.AddressVO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = UserApplication.class)
@RunWith(SpringRunner.class)
public class AddressTest {

    @Autowired
    private AddressService addressService;

    @Test
    public void testAddress(){

        AddressVO detail = addressService.detail(1);

        Assert.assertNotNull(detail);
    }
}
