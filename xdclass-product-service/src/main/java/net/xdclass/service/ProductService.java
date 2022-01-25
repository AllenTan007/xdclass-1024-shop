package net.xdclass.service;

import net.xdclass.model.ProductMessage;
import net.xdclass.request.LockProductRequest;
import net.xdclass.util.JsonData;
import net.xdclass.vo.ProductVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tanshiwei
 * @since 2021-12-12
 */
public interface ProductService {

    Map<String, Object> page(int page, int size);

    ProductVO detail(long productId);

    ProductVO findDetailById(long productId);

    List<ProductVO> findProductsByIdBatch(List<Long> productIdList);

    JsonData lockProductStock(LockProductRequest lockProductRequest);

    boolean releaseProductStock(ProductMessage productMessage);
}
