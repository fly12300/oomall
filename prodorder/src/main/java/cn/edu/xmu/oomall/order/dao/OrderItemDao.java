package cn.edu.xmu.oomall.order.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.order.dao.bo.OrderItem;
import cn.edu.xmu.oomall.order.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.order.mapper.OrderItemPoMapper;
import cn.edu.xmu.oomall.order.mapper.po.OrderItemPo;
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
public class OrderItemDao {
    private static final Logger logger = LoggerFactory.getLogger(OrderItemDao.class);
    private final static Long SAVE_FAILED_ID = -1L;
    private final static String KEY = "OI%d";
    private final static String ORDER_KEY = "OOI%d";
    private final RedisUtil redisUtil;
    private final OrderItemPoMapper orderItemPoMapper;
    private final GoodsDao goodsDao;
    @Value("${oomall.order.timeout}")
    private int timeout;

    @Autowired
    public OrderItemDao(RedisUtil redisUtil, OrderItemPoMapper orderItemPoMapper, GoodsDao goodsDao) {
        this.redisUtil = redisUtil;
        this.orderItemPoMapper = orderItemPoMapper;
        this.goodsDao = goodsDao;
    }

    public void setBo(OrderItem bo) {
        bo.setGoodsDao(goodsDao);
    }

    public OrderItem getBo(OrderItemPo po, Optional<String> redisKey) {
        OrderItem bo = cloneObj(po, OrderItem.class);
        this.setBo(bo);
        redisKey.ifPresent(key -> redisUtil.set(key, bo, timeout));
        return bo;
    }

    /**
     * 通过id查询订单明细
     *
     * @param id 订单明细id
     * @return OrderItem
     */
    public OrderItem findById(Long id) throws RuntimeException {
        logger.debug("findById: id = {}", id);
        if (null == id) {
            return null;
        }
        String key = String.format(KEY, id);
        if (redisUtil.hasKey(key)) {
            OrderItem bo = (OrderItem) redisUtil.get(key);
            this.setBo(bo);
            return bo;
        }
        Optional<OrderItemPo> ret = this.orderItemPoMapper.findById(id);
        if (ret.isPresent()) {
            return this.getBo(ret.get(), Optional.of(key));
        } else {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "订单", id));
        }
    }

    /**
     * 通过OrderId查询订单明细
     */
    public List<OrderItem> retrieveByOrderId(Long orderId) throws RuntimeException {
        logger.debug("findByOrderId: id = {}", orderId);
        if (null == orderId) {
            return null;
        }
        String key = String.format(ORDER_KEY, orderId);
        if (redisUtil.hasKey(key)) {
            List<Long> orderItemIds = (List<Long>) redisUtil.get(key);
            return orderItemIds.stream().map(this::findById).filter(Objects::nonNull).collect(Collectors.toList());
        }
        Pageable pageable = PageRequest.of(0, MAX_RETURN);
        List<OrderItem> boList = orderItemPoMapper.findByOrderId(orderId, pageable)
                .stream()
                .map(po -> this.getBo(po, Optional.of(String.format(KEY, po.getId()))))
                .collect(Collectors.toList());
        redisUtil.set(key, (ArrayList<Long>) boList.stream().map(OrderItem::getId).collect(Collectors.toList()), timeout);
        return boList;
    }

    /**
     * 新增订单明细
     *
     * @param bo
     * @param userDto
     */
    public void insert(OrderItem bo, UserDto userDto) throws RuntimeException {
        this.setBo(bo);
        bo.setId(null);
        OrderItemPo po = cloneObj(bo, OrderItemPo.class);
        putUserFields(po, "creator", userDto);
        putGmtFields(po, "create");
        logger.debug("save: po = {}", po);
        po = this.orderItemPoMapper.save(po);
        bo.setId(po.getId());
    }

    /**
     * 保存订单明细
     */
    public String save(OrderItem bo, UserDto user) throws RuntimeException {
        logger.debug("save: bo = {}", bo);
        OrderItemPo po = cloneObj(bo, OrderItemPo.class);
        putUserFields(po, "modifier", user);
        putGmtFields(po, "modified");
        logger.debug("save: po = {}", po);
        this.orderItemPoMapper.save(po);
        if (SAVE_FAILED_ID.equals(po.getId())) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "订单明细", bo.getId()));
        }
        return String.format(KEY, bo.getId());
    }
}
