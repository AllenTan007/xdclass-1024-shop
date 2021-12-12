package net.xdclass.controller;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.xdclass.enums.BizCodeEnum;
import net.xdclass.mapper.CouponRecordMapper;
import net.xdclass.service.CouponRecordService;
import net.xdclass.service.CouponService;
import net.xdclass.util.JsonData;
import net.xdclass.vo.CouponRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 谭世伟
 * @since 2021-11-27
 */
@RestController
@RequestMapping("/api/coupon_record/v1")
public class CouponRecordController {

    @Autowired
    private CouponRecordService couponRecordService;

    @ApiOperation("分页查询个人优惠券")
    @GetMapping("page")
    public JsonData page(@RequestParam(value = "page",defaultValue = "1")int page,
                         @RequestParam(value = "size",defaultValue = "20")int size){

        Map<String, Object> map = couponRecordService.page(page, size);
        return JsonData.buildSuccess(map);
    }

    @ApiOperation("查询优惠券记录详情")
    @GetMapping("detail/{record_id}")
    public JsonData getCouponRecordDetail(@ApiParam(value = "记录id")  @PathVariable("record_id") long recordId){



        CouponRecordVO couponRecordVO = couponRecordService.findById(recordId);

        return couponRecordVO == null ? JsonData.buildResult(BizCodeEnum.COUPON_NO_EXITS):JsonData.buildSuccess(couponRecordVO);

    }
}

