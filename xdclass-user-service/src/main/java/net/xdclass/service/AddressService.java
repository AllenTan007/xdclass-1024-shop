package net.xdclass.service;

import net.xdclass.model.AddressDO;
import net.xdclass.request.AddressAddReqeust;

public interface AddressService {

    AddressDO detail (long id);

    void addDetail(AddressAddReqeust addressAddReqeust);
}
