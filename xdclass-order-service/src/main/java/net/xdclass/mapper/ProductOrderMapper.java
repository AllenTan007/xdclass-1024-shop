package net.xdclass.mapper;

import net.xdclass.model.ProductOrderDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tanshiwei
 * @since 2022-01-14
 */
public interface ProductOrderMapper extends BaseMapper<ProductOrderDO> {

    void updateOrderPayState(@Param("state") String state, @Param("outTradeNo")String outTradeNo);
}
