package net.xdclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.xdclass.interceptor.LoginInterceptor;
import net.xdclass.mapper.AddressMapper;
import net.xdclass.model.AddressDO;
import net.xdclass.model.LoginUser;
import net.xdclass.request.AddressAddReqeust;
import net.xdclass.service.AddressService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;


    @Override
    public AddressDO detail(long id) {
        return addressMapper.selectOne(new QueryWrapper<AddressDO>().eq("id", id));
    }

    @Override
    public void addDetail(AddressAddReqeust addressAddReqeust) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        AddressDO addressDO = new AddressDO();
        BeanUtils.copyProperties(addressAddReqeust, addressDO);
        addressDO.setUserId(loginUser.getId());
        addressDO.setCreateTime(new Date());
        if (addressAddReqeust.getDefaultStatus() == 1) {
            AddressDO defaultAddressDO = addressMapper.selectOne(new QueryWrapper<AddressDO>().eq("user_id", loginUser.getId()).eq("default_status", 1));
            if (defaultAddressDO != null) {
                defaultAddressDO.setDefaultStatus(0);
                addressMapper.updateById(defaultAddressDO);
            }
        }

        int rows = addressMapper.insert(addressDO);
        log.info("新增收货地址:rows={},data={}",rows,addressDO);

    }
}
