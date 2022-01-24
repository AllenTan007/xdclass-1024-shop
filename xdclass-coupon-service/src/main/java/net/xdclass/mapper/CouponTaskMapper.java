package net.xdclass.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.xdclass.model.CouponTaskDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author tanshiwei
 * @since 2022-01-21
 */
public interface CouponTaskMapper extends BaseMapper<CouponTaskDO> {

    int insertBatch(@Param("couponTaskList") List<CouponTaskDO> couponTaskList);
}
