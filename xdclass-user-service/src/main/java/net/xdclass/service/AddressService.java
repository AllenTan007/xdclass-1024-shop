package net.xdclass.service;

import net.xdclass.request.AddressAddReqeust;
import net.xdclass.vo.AddressVO;

public interface AddressService {

    AddressVO detail (long id);

    void addDetail(AddressAddReqeust addressAddReqeust);

    int del(long addressId);
}
