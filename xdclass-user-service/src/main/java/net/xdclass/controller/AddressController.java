package net.xdclass.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.xdclass.model.AddressDO;
import net.xdclass.request.AddressAddReqeust;
import net.xdclass.service.AddressService;
import net.xdclass.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 电商-公司收发货地址表 前端控制器
 * </p>
 *
 * @author 二当家小D
 * @since 2021-08-28
 */
@Api("收货地址模块")
@RestController
@RequestMapping("/api/address/v1/")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping("find/{address_id}")
    @ApiOperation("根据id查询地址详情")
    public Object detail(
            @ApiParam(value = "地址id",required = true)
            @PathVariable("address_id") long id){
        AddressDO addressDO = addressService.detail(id);
        return JsonData.buildSuccess(addressDO);
    }

    @ApiOperation("新增地址详情")
    @PostMapping("add")
    public JsonData addDetail(@ApiParam("地址对象")
            @RequestBody AddressAddReqeust addressAddReqeust){
        addressService.addDetail(addressAddReqeust);
        return JsonData.buildSuccess();
    }


}

