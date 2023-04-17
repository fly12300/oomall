package cn.edu.xmu.oomall.order.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.order.dao.bo.OrderPayment;
import cn.edu.xmu.oomall.order.mapper.OrderPaymentPoMapper;
import cn.edu.xmu.oomall.order.mapper.po.OrderPaymentPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.MAX_RETURN;
import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
public class OrderPaymentDao {
    private static final Logger logger = LoggerFactory.getLogger(OrderPaymentDao.class);
    private final static String KEY = "OP%d";
    private final static String ORDER_KEY = "OOP%d";
    private final RedisUtil redisUtil;
    private final OrderPaymentPoMapper orderPaymentPoMapper;
    @Value("${oomall.order.timeout}")
    private int timeout;

    @Autowired
    public OrderPaymentDao(RedisUtil redisUtil, OrderPaymentPoMapper orderPaymentPoMapper) {
        this.redisUtil = redisUtil;
        this.orderPaymentPoMapper = orderPaymentPoMapper;
    }

    public OrderPayment getBo(OrderPaymentPo po, Optional<String> redisKey) {
        OrderPayment bo = cloneObj(po, OrderPayment.class);
        redisKey.ifPresent(key -> redisUtil.set(key, bo, timeout));
        return bo;
    }

    /**
     * 通过id查询订单明细
     *
     * @param id 订单明细id
     * @return OrderPayment
     */
    public OrderPayment findById(Long id) throws RuntimeException {
        logger.debug("findById: id = {}", id);
        if (null == id) {
            return null;
        }
        String key = String.format(KEY, id);
        if (redisUtil.hasKey(key)) {
            OrderPayment bo = (OrderPayment) redisUtil.get(key);
            return bo;
        }
        Optional<OrderPaymentPo> ret = this.orderPaymentPoMapper.findById(id);
        if (ret.isPresent()) {
            return this.getBo(ret.get(), Optional.of(key));
        } else {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "订单支付", id));
        }
    }

    /**
     * 通过OrderId查询订单明细
     */
    public List<OrderPayment> retrieveByOrderId(Long orderId) throws RuntimeException {
        logger.debug("findByOrderId: id = {}", orderId);
        if (null == orderId) {
            return null;
        }
        String key = String.format(ORDER_KEY, orderId);
        if (redisUtil.hasKey(key)) {
            List<Long> orderPaymentIds = (List<Long>) redisUtil.get(key);
            return orderPaymentIds.stream().map(this::findById).filter(Objects::nonNull).collect(Collectors.toList());
        }
        Pageable pageable = PageRequest.of(0, MAX_RETURN);
        List<OrderPayment> boList = this.orderPaymentPoMapper.findByOrderId(orderId, pageable)
                .stream()
                .map(po -> this.getBo(po, Optional.of(String.format(KEY, po.getId()))))
                .collect(Collectors.toList());
        redisUtil.set(key, (ArrayList<Long>) boList.stream().map(OrderPayment::getId).collect(Collectors.toList()), timeout);
        return boList;
    }

    /**
     * 新建订单支付
     *
     * @param bo      订单bo
     * @param userDto 创建者
     */
    public void insert(OrderPayment bo, UserDto userDto) throws RuntimeException {
        bo.setId(null);
        OrderPaymentPo po = cloneObj(bo, OrderPaymentPo.class);
        putUserFields(po, "creator", userDto);
        putGmtFields(po, "create");
        logger.debug("save: po = {}", po);
        po = this.orderPaymentPoMapper.save(po);
        bo.setId(po.getId());
    }
}
