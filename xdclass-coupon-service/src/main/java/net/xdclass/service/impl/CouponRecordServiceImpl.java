package net.xdclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import net.xdclass.config.RabbitMQConfig;
import net.xdclass.enums.BizCodeEnum;
import net.xdclass.enums.CouponStateEnum;
import net.xdclass.enums.ProductOrderStateEnum;
import net.xdclass.enums.StockTaskStateEnum;
import net.xdclass.exception.BizException;
import net.xdclass.feign.ProductOrderFeignSerivce;
import net.xdclass.interceptor.LoginInterceptor;
import net.xdclass.mapper.CouponRecordMapper;
import net.xdclass.mapper.CouponTaskMapper;
import net.xdclass.model.CouponRecordDO;
import net.xdclass.model.CouponRecordMessage;
import net.xdclass.model.CouponTaskDO;
import net.xdclass.model.LoginUser;
import net.xdclass.request.LockCouponRecordRequest;
import net.xdclass.service.CouponRecordService;
import net.xdclass.util.JsonData;
import net.xdclass.vo.CouponRecordVO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 谭世伟
 * @since 2021-11-27
 */
@Service
@Slf4j
public class CouponRecordServiceImpl implements CouponRecordService {


    @Autowired
    private CouponRecordMapper couponRecordMapper;
    @Autowired
    private CouponTaskMapper couponTaskMapper;
    @Autowired
    private ProductOrderFeignSerivce productOrderFeignSerivce;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @Override
    public Map<String, Object> page(int page, int size) {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        //封装分页信息
        Page<CouponRecordDO> pageInfo = new Page<>(page, size);
        Page<CouponRecordDO> recordDOIPage = couponRecordMapper.selectPage(pageInfo, new QueryWrapper<CouponRecordDO>()
                .eq("user_id", loginUser.getId())
                .orderByDesc("create_time"));
        Map<String, Object> pageMap = new HashMap<>(3);
        pageMap.put("total_record", recordDOIPage.getTotal());
        pageMap.put("total_page", recordDOIPage.getPages());
        pageMap.put("current_data", recordDOIPage.getRecords().stream().map(this::beanProcess).collect(Collectors.toList()));
        return pageMap;
    }

    @Override
    public CouponRecordVO findById(long recordId) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        CouponRecordDO couponRecordDO = couponRecordMapper.selectOne(new QueryWrapper<CouponRecordDO>()
                .eq("user_id", loginUser.getId())
                .eq("id", recordId));

        if (couponRecordDO == null) {
            return null;
        }
        return beanProcess(couponRecordDO);
    }

    @Override
    public boolean releaseCouponRecord(CouponRecordMessage recordMessage) {
        CouponTaskDO couponTaskDO = couponTaskMapper.selectOne(new QueryWrapper<CouponTaskDO>().eq("id", recordMessage.getTaskId()));

        if (couponTaskDO == null) {
            log.warn("工作单不存，消息:{}", recordMessage);
            return true;
        }
        if (StockTaskStateEnum.LOCK.name().equalsIgnoreCase(couponTaskDO.getLockState())) {

            JsonData jsonData = productOrderFeignSerivce.queryProductOrderState(recordMessage.getOutTradeNo());
            if (jsonData.getCode() == 0) {
                String state = jsonData.getData().toString();
                if (ProductOrderStateEnum.PAY.name().equalsIgnoreCase(state)) {
                    couponTaskDO.setLockState(StockTaskStateEnum.FINISH.name());
                    couponTaskMapper.update(couponTaskDO, new QueryWrapper<CouponTaskDO>().eq("id", couponTaskDO.getId()));
                    log.info("订单已经支付，修改库存锁定工作单FINISH状态:{}", recordMessage);
                    return true;
                }
                if (ProductOrderStateEnum.NEW.name().equalsIgnoreCase(state)) {
                    log.info("订单状态是NEW，返回给消息队列，重新投递:{}", recordMessage);
                    return false;
                }
            }
            couponTaskDO.setLockState(StockTaskStateEnum.CANCEL.name());
            couponTaskMapper.update(couponTaskDO, new QueryWrapper<CouponTaskDO>().eq("id", couponTaskDO.getId()));
            couponRecordMapper.updateState(couponTaskDO.getCouponRecordId(), CouponStateEnum.NEW.name());
            log.info("订单已经取消，修改库存锁定工作单CANCEL状态:{}", recordMessage);
            return true;

        } else {
            log.warn("工作单状态不是LOCK,state={},消息体={}", couponTaskDO.getLockState(), recordMessage);
            return true;
        }
    }

    @Override
    @Transactional
    public JsonData lockCouponRecords(LockCouponRecordRequest recordRequest) {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        String orderOutTradeNo = recordRequest.getOrderOutTradeNo();
        List<Long> lockCouponRecordIds = recordRequest.getLockCouponRecordIds();

        int updateRows = couponRecordMapper.lockUseStateBatch(loginUser.getId(), CouponStateEnum.USED.name(), lockCouponRecordIds);
        List<CouponTaskDO> couponTaskDOList = lockCouponRecordIds.stream().map(obj -> {
            CouponTaskDO couponTaskDO = new CouponTaskDO();
            couponTaskDO.setLockState(StockTaskStateEnum.LOCK.name());
            couponTaskDO.setCouponRecordId(obj);
            couponTaskDO.setOutTradeNo(orderOutTradeNo);
            couponTaskDO.setCreateTime(new Date());
            return couponTaskDO;
        }).collect(Collectors.toList());
        int insertRows = couponTaskMapper.insertBatch(couponTaskDOList);
        if (updateRows != lockCouponRecordIds.size() || insertRows != updateRows) {
            throw new BizException(BizCodeEnum.COUPON_RECORD_LOCK_FAIL);
        } else {
            for (CouponTaskDO couponTaskDO : couponTaskDOList) {
                CouponRecordMessage couponRecordMessage = new CouponRecordMessage();
                couponRecordMessage.setOutTradeNo(orderOutTradeNo);
                couponRecordMessage.setTaskId(couponTaskDO.getId());

                rabbitTemplate.convertAndSend(rabbitMQConfig.getEventExchange(), rabbitMQConfig.getCouponReleaseDelayRoutingKey(), couponRecordMessage);
                log.info("优惠券锁定消息发送成功:{}", couponRecordMessage.toString());
            }
            return JsonData.buildSuccess();
        }

    }

    private CouponRecordVO beanProcess(CouponRecordDO couponRecordDO) {
        CouponRecordVO couponRecordVO = new CouponRecordVO();
        BeanUtils.copyProperties(couponRecordDO, couponRecordVO);
        return couponRecordVO;
    }
}
