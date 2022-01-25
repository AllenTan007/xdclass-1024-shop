package net.xdclass.mapper;

import net.xdclass.model.ProductDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tanshiwei
 * @since 2021-12-12
 */
public interface ProductMapper extends BaseMapper<ProductDO> {

    int lockProductStock(@Param("productId") long productId, @Param("buyNum") int buyNum);

    int releaseProductStock(@Param("productId") long productId, @Param("buyNum") int buyNum);
}
