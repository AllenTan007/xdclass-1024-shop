package net.xdclass.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.xdclass.model.ProductDO;
import net.xdclass.mapper.ProductMapper;
import net.xdclass.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tanshiwei
 * @since 2021-12-12
 */
@Service
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, ProductDO> implements ProductService {

}
