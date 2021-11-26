package net.xdclass.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.xdclass.enums.BizCodeEnum;
import net.xdclass.request.AddressAddReqeust;
import net.xdclass.service.AddressService;
import net.xdclass.util.JsonData;
import net.xdclass.vo.AddressVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public JsonData detail(
            @ApiParam(value = "地址id", required = true)
            @PathVariable("address_id") long addressId) {
        AddressVO addressVO = addressService.detail(addressId);
        return addressVO != null ? JsonData.buildSuccess(addressVO) : JsonData.buildResult(BizCodeEnum.ADDRESS_NO_EXITS);
    }

    @ApiOperation("删除地址详情")
    @GetMapping("delete/{addressId}")
    public JsonData deteleDetail(@ApiParam("地址id")
                                 @PathVariable("addressId") long addressId) {
        int rows = addressService.del(addressId);
        return rows == 1 ? JsonData.buildSuccess() : JsonData.buildResult(BizCodeEnum.ADDRESS_DEL_FAIL);
    }

    @ApiOperation("新增地址详情")
    @PostMapping("add")
    public JsonData addDetail(@ApiParam("地址对象")
                              @RequestBody AddressAddReqeust addressAddReqeust) {
        addressService.addDetail(addressAddReqeust);
        return JsonData.buildSuccess();
    }

    /**
     * 查询用户的全部收费地址
     * @return
     */
    @ApiOperation("查询用户的全部收费地址")
    @GetMapping("/list")
    public JsonData findUserAllAddress(){

        List<AddressVO> list = addressService.listUserAllAddress();
        return JsonData.buildSuccess(list);

    }

}

