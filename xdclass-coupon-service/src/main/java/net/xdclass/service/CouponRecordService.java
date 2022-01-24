package net.xdclass.service;

import net.xdclass.model.CouponRecordMessage;
import net.xdclass.request.LockCouponRecordRequest;
import net.xdclass.util.JsonData;
import net.xdclass.vo.CouponRecordVO;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tanshiwei
 * @since 2021-11-27
 */
public interface CouponRecordService {

    Map<String, Object> page(int page, int size);

    CouponRecordVO findById(long recordId);

    boolean releaseCouponRecord(CouponRecordMessage recordMessage);

    JsonData lockCouponRecords(LockCouponRecordRequest recordRequest);
}

