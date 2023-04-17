package cn.edu.xmu.oomall.order.dao;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.order.dao.bo.OrderRefund;
import cn.edu.xmu.oomall.order.mapper.OrderRefundPoMapper;
import cn.edu.xmu.oomall.order.mapper.po.OrderRefundPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
public class OrderRefundDao {
    private static final Logger logger = LoggerFactory.getLogger(OrderPaymentDao.class);

    private final OrderRefundPoMapper orderRefundPoMapper;

    @Autowired
    public OrderRefundDao(OrderRefundPoMapper orderRefundPoMapper) {
        this.orderRefundPoMapper = orderRefundPoMapper;
    }

    /**
     * 新建退款订单
     *
     * @param bo      订单bo
     * @param userDto 创建者
     */
    public void insert(OrderRefund bo, UserDto userDto) throws RuntimeException {
        bo.setId(null);
        OrderRefundPo po = cloneObj(bo, OrderRefundPo.class);
        putUserFields(po, "creator", userDto);
        putGmtFields(po, "create");
        logger.debug("save: po = {}", po);
        po = this.orderRefundPoMapper.save(po);
        bo.setId(po.getId());
    }
}
