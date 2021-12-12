package net.xdclass.service;

import net.xdclass.vo.CouponRecordVO;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 谭世伟
 * @since 2021-11-27
 */
public interface CouponRecordService {

    Map<String, Object> page(int page, int size);

    CouponRecordVO findById(long recordId);
}
