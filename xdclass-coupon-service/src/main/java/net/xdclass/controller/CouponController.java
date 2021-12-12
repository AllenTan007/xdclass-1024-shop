package net.xdclass.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import net.xdclass.enums.CouponCategoryEnum;
import net.xdclass.request.NewUserCouponRequest;
import net.xdclass.service.CouponService;
import net.xdclass.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 谭世伟
 * @since 2021-11-27
 */
@Api("优惠券模块")
@RestController
@RequestMapping("/api/coupon/v1")
@Slf4j
public class CouponController {


    @Autowired
    private CouponService couponService;

    @ApiOperation("分页查询优惠券")
    @GetMapping("page_coupon")
    public JsonData pageCouponList(@ApiParam(value = "当前页") @RequestParam(value = "page", defaultValue = "1") int page,
                                   @ApiParam(value = "每页显示多少条数") @RequestParam(value = "size", defaultValue = "10") int size) {

        Map<String, Object> map = couponService.pageCouponActivity(page, size);
        return JsonData.buildSuccess(map);
    }

    @ApiOperation("领取优惠券")
    @GetMapping("/add/promotion/{coupon_id}")
    public JsonData addPromotionCoupon(@ApiParam(value = "优惠券id", required = true) @PathVariable("coupon_id") long couponId) {

        JsonData jsonData = couponService.addCoupon(couponId, CouponCategoryEnum.PROMOTION);

        return jsonData;
    }

    @ApiOperation("RPC-新用户注册接口")
    @PostMapping("/new_coupon")
    public JsonData addNewUserCoupon(@ApiParam(value = "用户对象", required = true)@RequestBody NewUserCouponRequest newUserCouponRequest) {

        return couponService.getCouponByNewUser(newUserCouponRequest);
    }

}

