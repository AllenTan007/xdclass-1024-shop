package net.xdclass.mapper;

import net.xdclass.model.UserDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 二当家小D
 * @since 2021-08-28
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {

}
