package net.xdclass.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.xdclass.enums.CouponCategoryEnum;
import net.xdclass.model.CouponDO;
import net.xdclass.request.NewUserCouponRequest;
import net.xdclass.util.JsonData;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 谭世伟
 * @since 2021-11-27
 */
public interface CouponService extends IService<CouponDO> {

    Map<String,Object> pageCouponActivity(int page, int size);

    JsonData addCoupon(long couponId, CouponCategoryEnum promotion);

    JsonData getCouponByNewUser(NewUserCouponRequest newUserCouponRequest);
}
