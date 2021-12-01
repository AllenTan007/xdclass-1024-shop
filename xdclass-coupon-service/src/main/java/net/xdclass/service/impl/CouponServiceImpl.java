package net.xdclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.xdclass.enums.BizCodeEnum;
import net.xdclass.enums.CouponCategoryEnum;
import net.xdclass.enums.CouponPublishEnum;
import net.xdclass.exception.BizException;
import net.xdclass.interceptor.LoginInterceptor;
import net.xdclass.mapper.CouponMapper;
import net.xdclass.mapper.CouponRecordMapper;
import net.xdclass.model.CouponDO;
import net.xdclass.model.CouponRecordDO;
import net.xdclass.model.LoginUser;
import net.xdclass.service.CouponService;
import net.xdclass.util.CommonUtil;
import net.xdclass.util.JsonData;
import net.xdclass.vo.CouponVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 谭世伟
 * @since 2021-11-27
 */
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, CouponDO> implements CouponService {

    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private CouponRecordMapper couponRecordMapper;


    @Override
    public Map<String, Object> pageCouponActivity(int page, int size) {

        Page<CouponDO> pageInfo = new Page<>(page, size);
        IPage<CouponDO> couponDOIPage = couponMapper.selectPage(pageInfo, new QueryWrapper<CouponDO>().eq("publish", CouponPublishEnum.PUBLISH)
                .eq("category", CouponCategoryEnum.PROMOTION)
                .orderByDesc("create_time"));

        Map<String, Object> pageMap = new HashMap<>(3);
        //总条数
        pageMap.put("total_record", couponDOIPage.getTotal());
        //总页数
        pageMap.put("total_page", couponDOIPage.getPages());

        pageMap.put("current_date", couponDOIPage.getRecords().stream().map(this::beanProcess).collect(Collectors.toList()));
        return pageMap;
    }

    @Override
    public JsonData addCoupon(long couponId, CouponCategoryEnum couponCategory) {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        CouponDO couponDO = couponMapper.selectOne(new QueryWrapper<CouponDO>()
                .eq("id", couponId)
                .eq("category", couponCategory.name())
                .eq("publish", CouponPublishEnum.PUBLISH));


        // 检查优惠券是否领取
        this.checkCoupon(couponDO, loginUser.getId());


        return JsonData.buildSuccess();
    }

    private void checkCoupon(CouponDO couponDO, Long id) {

        if (couponDO == null) {
            throw new BizException(BizCodeEnum.COUPON_NO_EXITS);
        }
        //库存是否足够
        if (couponDO.getStock() <= 0) {
            throw new BizException(BizCodeEnum.COUPON_NO_STOCK);
        }
        //判断是否是否发布状态
        if (!CouponPublishEnum.PUBLISH.name().equals(couponDO.getPublish())) {
            throw new BizException(BizCodeEnum.COUPON_GET_FAIL);
        }

        //是否在领取时间范围
        long time = CommonUtil.getCurrentTimestamp();
        long startTime = couponDO.getStartTime().getTime();
        long endTime = couponDO.getEndTime().getTime();
        if (time < startTime || time > endTime) {
            throw new BizException(BizCodeEnum.COUPON_OUT_OF_TIME);
        }

        //用户是否超过限制
        Integer recordNum = couponRecordMapper.selectCount(new QueryWrapper<CouponRecordDO>()
                .eq("user_id", id)
                .eq("coupon_id", couponDO.getId()));
        if (recordNum >= couponDO.getUserLimit()) {
            throw new BizException(BizCodeEnum.COUPON_OUT_OF_LIMIT);
        }
    }

    private CouponVO beanProcess(CouponDO couponDO) {
        CouponVO couponVO = new CouponVO();
        BeanUtils.copyProperties(couponDO, couponVO);
        return couponVO;
    }
}
