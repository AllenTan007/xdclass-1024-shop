package net.xdclass.mapper;

import net.xdclass.model.CouponRecordDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 谭世伟
 * @since 2021-11-27
 */
public interface CouponRecordMapper extends BaseMapper<CouponRecordDO> {

    void updateState(@Param("couponId") Long couponId,@Param("useState") String useState);

    int lockUseStateBatch(@Param("userId") Long userId, @Param("useState") String useState, @Param("lockCouponRecordIds") List<Long> lockCouponRecordIds);
}
