//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.order.dao.bo.Order;
import cn.edu.xmu.oomall.order.dao.openfeign.CustomerDao;
import cn.edu.xmu.oomall.order.dao.openfeign.ExpressDao;
import cn.edu.xmu.oomall.order.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.order.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.order.mapper.OrderPoMapper;
import cn.edu.xmu.oomall.order.mapper.po.OrderPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
public class OrderDao {
    private final static Logger logger = LoggerFactory.getLogger(OrderDao.class);
    private final static String KEY = "O%d";
    private final static Long SAVE_FAILED_ID = -1L;
    private final RedisUtil redisUtil;
    private final OrderPoMapper orderPoMapper;
    private final CustomerDao customerDao;
    private final ShopDao shopDao;
    private final ExpressDao expressDao;
    private final OrderItemDao orderItemDao;
    private final GoodsDao goodsDao;
    private final OrderPaymentDao orderPaymentDao;
    @Value("${oomall.order.timeout}")
    private int timeout;


    @Autowired
    public OrderDao(RedisUtil redisUtil, OrderPoMapper orderPoMapper, CustomerDao customerDao, ShopDao shopDao, ExpressDao expressDao, OrderItemDao orderItemDao, GoodsDao goodsDao, OrderPaymentDao orderPaymentDao) {
        this.redisUtil = redisUtil;
        this.orderPoMapper = orderPoMapper;
        this.customerDao = customerDao;
        this.shopDao = shopDao;
        this.expressDao = expressDao;
        this.orderItemDao = orderItemDao;
        this.goodsDao = goodsDao;
        this.orderPaymentDao = orderPaymentDao;
    }

    public void setBo(Order bo) {
        bo.setCustomerDao(this.customerDao);
        bo.setExpressDao(this.expressDao);
        bo.setShopDao(this.shopDao);
        bo.setOrderItemDao(this.orderItemDao);
        bo.setGoodsDao(this.goodsDao);
        bo.setOrderPaymentDao(this.orderPaymentDao);
    }

    public Order getBo(OrderPo po, Optional<String> redisKey) {
        Order bo = cloneObj(po, Order.class);
        this.setBo(bo);
        redisKey.ifPresent(key -> redisUtil.set(key, bo, timeout));
        return bo;
    }

    /**
     * 新建订单
     *
     * @param bo      订单bo
     * @param userDto 创建者
     */
    public void insert(Order bo, UserDto userDto) throws RuntimeException {
        this.setBo(bo);
        bo.setId(null);
        OrderPo po = cloneObj(bo, OrderPo.class);
        putUserFields(po, "creator", userDto);
        putGmtFields(po, "create");
        logger.debug("save: po = {}", po);
        po = this.orderPoMapper.save(po);
        bo.setId(po.getId());
    }

    /**
     * 查询订单
     */
    public List<Order> retrieveOrders(Long shopId, String orderSn, Integer status, LocalDateTime beginTime,
                                      LocalDateTime endTime, Integer page, Integer pageSize, Long customerId) throws RuntimeException {
        logger.debug("retrieveByCustomerId: customerId = {}", customerId);
        if (null == customerId) {
            return null;
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        List<Order> boList = this.orderPoMapper.findOrderPos(customerId, orderSn, status, beginTime, endTime, shopId, pageable)
                .stream()
                .map(po -> cloneObj(po, Order.class))
                .collect(Collectors.toList());
        logger.debug("retrieveByCustomerId: boList = {}", boList);
        return boList;
    }

    /**
     * 通过id查询订单
     *
     * @param id 订单id
     * @return Order
     */
    public Order findById(Long id) throws RuntimeException {
        logger.debug("findById: id = {}", id);
        if (null == id) {
            return null;
        }
        String key = String.format(KEY, id);
        if (redisUtil.hasKey(key)) {
            Order bo = (Order) redisUtil.get(key);
            this.setBo(bo);
            return bo;
        }
        Optional<OrderPo> ret = this.orderPoMapper.findById(id);
        if (ret.isPresent()) {
            return this.getBo(ret.get(), Optional.of(key));
        } else {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "订单", id));
        }
    }

    /**
     * 修改订单
     *
     * @param bo   订单
     * @param user 修改人
     * @return redis key
     */
    public String save(Order bo, UserDto user) throws RuntimeException {
        logger.debug("save: bo = {}", bo);
        OrderPo po = cloneObj(bo, OrderPo.class);
        putUserFields(po, "modifier", user);
        putGmtFields(po, "modified");
        logger.debug("save: po = {}", po);
        this.orderPoMapper.save(po);
        if (SAVE_FAILED_ID.equals(po.getId())) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "订单", bo.getId()));
        }
        return String.format(KEY, bo.getId());
    }
}
