package net.xdclass.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.xdclass.model.ProductOrderItemDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author tanshiwei
 * @since 2022-01-14
 */
public interface ProductOrderItemMapper extends BaseMapper<ProductOrderItemDO> {

    void insertBatch(@Param("productOrderItemList") List<ProductOrderItemDO> productOrderItemList);
}
