//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.Common;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.order.dao.OrderDao;
import cn.edu.xmu.oomall.order.dao.OrderItemDao;
import cn.edu.xmu.oomall.order.dao.OrderPaymentDao;
import cn.edu.xmu.oomall.order.dao.OrderRefundDao;
import cn.edu.xmu.oomall.order.dao.bo.Order;
import cn.edu.xmu.oomall.order.dao.bo.OrderItem;
import cn.edu.xmu.oomall.order.dao.bo.OrderPayment;
import cn.edu.xmu.oomall.order.dao.bo.OrderRefund;
import cn.edu.xmu.oomall.order.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.order.dao.openfeign.bo.Coupon;
import cn.edu.xmu.oomall.order.dao.openfeign.bo.Onsale;
import cn.edu.xmu.oomall.order.service.dto.*;
import cn.edu.xmu.oomall.order.service.openfeign.PaymentService;
import cn.edu.xmu.oomall.order.service.openfeign.dto.IdDto;
import cn.edu.xmu.oomall.order.service.openfeign.dto.PaymentDto;
import cn.edu.xmu.oomall.order.service.openfeign.vo.PaymentVo;
import cn.edu.xmu.oomall.order.service.openfeign.vo.RefundVo;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;
import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

@Repository
public class OrderService {
    private final static Logger logger = LoggerFactory.getLogger(OrderService.class);
    @Value("${oomall.order.server-num}")
    private int serverNum;

    private final GoodsDao goodsDao;

    private final OrderDao orderDao;

    private final OrderItemDao orderItemDao;
    private final OrderRefundDao orderRefundDao;

    private final RocketMQTemplate rocketMQTemplate;

    private final PaymentService paymentService;

    private final OrderPaymentDao orderPaymentDao;

    private final RedisUtil redisUtil;

    @Autowired
    public OrderService(GoodsDao goodsDao, OrderDao orderDao, OrderItemDao orderItemDao, OrderRefundDao orderRefundDao, RocketMQTemplate rocketMQTemplate, PaymentService paymentService, OrderPaymentDao orderPaymentDao, RedisUtil redisUtil) {
        this.goodsDao = goodsDao;
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
        this.orderRefundDao = orderRefundDao;
        this.rocketMQTemplate = rocketMQTemplate;
        this.paymentService = paymentService;
        this.orderPaymentDao = orderPaymentDao;
        this.redisUtil = redisUtil;
    }

    @Transactional
    public Map<Long, List<OrderItem>> packOrder(List<SimpleOrderItemDto> items, UserDto customer) {
        Map<Long, List<OrderItem>> packs = new HashMap<>();
        items.forEach(item -> {
            InternalReturnObject<Onsale> retObj = this.goodsDao.getOnsaleById(PLATFORM, item.getOnsaleId());
            if (retObj.getErrno() != ReturnNo.OK.getErrNo()) {
                throw new BusinessException(ReturnNo.getReturnNoByCode(retObj.getErrno()), retObj.getErrmsg());
            }
            Onsale onsale = retObj.getData();
            OrderItem orderItem = OrderItem.builder().onsaleId(onsale.getId()).price(onsale.getPrice()).name(onsale.getProduct().getName()).build();
            //确认actId正确
            if (null != onsale.getActList() && null != item.getActId()) {
                onsale.getActList().forEach(activity -> {
                    if (activity.getId().equals(item.getActId())) {
                        orderItem.setActivityId(item.getActId());
                    }
                    if (activity.getId().equals(item.getCouponId())) {
                        orderItem.setCouponId(item.getCouponId());
                    }
                });
            }
            //确认购买数正确
            if (item.getQuantity() <= onsale.getMaxQuantity() && item.getQuantity() <= onsale.getQuantity()) {
                //不能超过最大可购买数量
                orderItem.setQuantity(item.getQuantity());
            } else {
                throw new BusinessException(ReturnNo.ITEM_OVERMAXQUANTITY, String.format(ReturnNo.ITEM_OVERMAXQUANTITY.getMessage(), onsale.getId(), item.getQuantity(), onsale.getMaxQuantity()));
            }
            Long shopId = onsale.getShop().getId();
            List<OrderItem> pack = packs.get(shopId);
            if (null == pack) {
                packs.put(shopId, new ArrayList<>() {
                    {
                        add(orderItem);
                    }
                });
            } else {
                pack.add(orderItem);
            }
        });
        return packs;
    }

    @Transactional
    public void saveOrder(Map<Long, List<OrderItem>> packs, ConsigneeDto consignee, String message, UserDto customer) {
        packs.keySet().forEach(shopId -> {
                    Order order = Order.builder().customerId(customer.getId()).shopId(shopId).
                            consignee(consignee.getName()).address(consignee.getAddress()).mobile(consignee.getMobile()).regionId(consignee.getRegionId()).
                            orderSn(Common.genSeqNum(serverNum)).message(message).build();
                    this.orderDao.insert(order, customer);
                    packs.get(shopId).forEach(orderItem -> {
                        orderItem.setOrderId(order.getId());
                        this.orderItemDao.insert(orderItem, customer);
                    });
                }
        );
    }

    @Transactional
    public void createOrder(List<SimpleOrderItemDto> items, ConsigneeDto consignee, String message, UserDto customer) {
        Map<Long, List<OrderItem>> packs = this.packOrder(items, customer);

        String packStr = JacksonUtil.toJson(packs);
        Message<String> msg = MessageBuilder.withPayload(packStr).setHeader("consignee", consignee).setHeader("message", message).setHeader("user", customer).build();
        rocketMQTemplate.sendMessageInTransaction("order-create-topic", msg, null);
    }

    @Transactional
    public List<SimpleOrderDto> retrieveOrders(Long shopId, String orderSn, Integer status,
                                               LocalDateTime beginTime, LocalDateTime endTime,
                                               Integer page, Integer pageSize, Long customerId) {
        List<SimpleOrderDto> dtoList = this.orderDao.retrieveOrders(shopId, orderSn, status, beginTime, endTime, page, pageSize, customerId)
                .stream()
                .map(bo -> cloneObj(bo, SimpleOrderDto.class))
                .collect(Collectors.toList());
        logger.debug("retrieveOrders: dtoList = {}", dtoList);
        return dtoList;
    }

    /**
     * 顾客查询订单完整信息
     *
     * @param id         订单id
     * @param customerId 顾客id
     * @return OrderDto
     */
    @Transactional
    public OrderDto findOrderById(Long id, Long customerId, Long shopId) {
        logger.debug("getOrderById: id = {}, customerId = {}", id, customerId);
        Order bo = this.orderDao.findById(id);
        if (customerId != null && !bo.getCustomerId().equals(customerId)) {
            throw new BusinessException(ReturnNo.AUTH_NO_RIGHT);
        }
        if (shopId != null && !bo.getShopId().equals(shopId)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "订单", id, shopId));
        }
        OrderDto dto = cloneObj(bo, OrderDto.class);
        dto.setConsignee(new ConsigneeDto(bo.getConsignee(), bo.getAddress(), bo.getRegionId(), bo.getMobile()));
        dto.setCustomer(cloneObj(bo.getCustomer(), IdNameDto.class));
        dto.setShop(cloneObj(bo.getShop(), IdNameTypeDto.class));
        dto.setPack(cloneObj(bo.getPack(), SimplePackageDto.class));
        dto.setOrderItems(bo.getOrderItems().stream().map(orderItem -> {
            OrderItemDto orderItemDto = cloneObj(orderItem, OrderItemDto.class);
            orderItemDto.setProductId(orderItem.getProduct().getId());
            Coupon coupon = orderItem.getCoupon();
            if (null != coupon) {
                orderItemDto.setCoupon(new SimpleCouponDto(coupon.getId(), new SimpleCouponActivityDto(coupon.getId(), coupon.getName(), coupon.getQuantity(), coupon.getCouponTime()), coupon.getName(), ""));
            }
            return orderItemDto;
        }).collect(Collectors.toList()));
        logger.debug("getOrderById: dto = {}", dto);
        return dto;
    }

    /**
     * 修改订单
     *
     * @param id      订单id
     * @param userDto 修改人
     */
    @Transactional
    public void updateOrderConsigneeById(Long id, UserDto userDto, String consignee, Long regionId, String address, String mobile) {
        logger.debug("updateOrderById: id = {}, userDto = {}", id, userDto);
        Order bo = this.orderDao.findById(id);
        if (!bo.getCustomerId().equals(userDto.getId())) {
            throw new BusinessException(ReturnNo.AUTH_NO_RIGHT);
        }
        if (bo.getStatus() >= Order.DELIVERED) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, String.format(ReturnNo.STATENOTALLOW.getMessage(), "订单", id, bo.getStatusName()));
        }
        bo.setConsignee(consignee);
        bo.setRegionId(regionId);
        bo.setAddress(address);
        bo.setMobile(mobile);
        this.updateOrder(bo, userDto);
    }

    /**
     * 修改订单留言
     *
     * @param id      订单id
     * @param shopId  商铺id
     * @param userDto 用户
     * @param message 留言内容
     */
    @Transactional
    public void updateOrderMessageById(Long id, Long shopId, UserDto userDto, String message) {
        logger.debug("updateOrderMessageById: id = {}, userDto = {}, message = {}", id, userDto, message);
        Order bo = this.orderDao.findById(id);
        if (!bo.getShopId().equals(shopId)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "订单", id, shopId));
        }
        bo.setMessage(message);
        this.updateOrder(bo, userDto);
    }

    /**
     * 顾客取消订单
     *
     * @param id      订单id
     * @param userDto 用户
     */
    @Transactional
    public void cancelOrderById(Long id, UserDto userDto, boolean isShop) {
        logger.debug("cancelOrderById: id = {}, userDto = {}", id, userDto);
        Order bo = this.orderDao.findById(id);
        if (!isShop && !bo.getCustomerId().equals(userDto.getId())) {
            throw new BusinessException(ReturnNo.AUTH_NO_RIGHT);
        }
        if (isShop && !bo.getShopId().equals(userDto.getDepartId())) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "订单", id, userDto.getDepartId()));
        }
        if (!bo.allowStatus(Order.CANCELED) && !bo.allowStatus(Order.REFUNDING)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, String.format(ReturnNo.STATENOTALLOW.getMessage(), "订单", id, bo.getStatusName()));
        }
        Map<String, Object> msgMap = new HashMap<>();
        msgMap.put("body", bo);
        msgMap.put("userDto", userDto);
        Message<Map<String, Object>> message = MessageBuilder.withPayload(msgMap).build();
        this.rocketMQTemplate.asyncSend("order-cancel-topic", message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                logger.debug("order-cancel-topic send successful. orderId = {}", id);
            }

            @Override
            public void onException(Throwable e) {
                throw (BusinessException) e;
            }
        });
    }

    @Transactional
    public void cancelOrderExecute(Order order, UserDto userDto) {
        if (order.allowStatus(Order.CANCELED)) {
            order.setStatus(Order.CANCELED);
        } else {
            order.setStatus(Order.REFUNDING);
            this.createRefund(order, userDto);
        }

        this.updateOrder(order, userDto);
    }

    @Transactional
    public void updateOrder(Order bo, UserDto userDto) {
        logger.debug("updateOrder: bo = {}, userDto = {}", bo, userDto);
        String key = this.orderDao.save(bo, userDto);
        this.redisUtil.del(key);
    }

    @Transactional
    public void updateOrderItem(OrderItem bo, UserDto userDto) {
        logger.debug("updateOrderItem: bo = {}, userDto = {}", bo, userDto);
        String key = this.orderItemDao.save(bo, userDto);
        this.redisUtil.del(key);
    }

    @Transactional
    public void payOrder(Long id, UserDto userDto, Long point, Long shopChannelId, List<Long> coupons) {
        logger.debug("payOrder: id = {}, userDto = {}, point = {}", id, userDto, point);
        Order bo = this.orderDao.findById(id);
        if (!bo.getCustomerId().equals(userDto.getId())) {
            throw new BusinessException(ReturnNo.AUTH_NO_RIGHT);
        }
        if (bo.getStatus() >= 200) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, String.format(ReturnNo.STATENOTALLOW.getMessage(), "订单", id, bo.getStatusName()));
        }
        Map<String, Object> msgMap = new HashMap<>() {
            {
                put("body", bo);
                put("userDto", userDto);
                put("point", point);
                put("shopChannelId", shopChannelId);
                put("coupons", coupons);
            }
        };
        Message<Map<String, Object>> message = MessageBuilder.withPayload(msgMap).build();
        this.rocketMQTemplate.asyncSend("order-pay-topic", message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                logger.debug("order-pay-topic send successful. orderId = {}", id);
            }

            @Override
            public void onException(Throwable e) {
                throw (BusinessException) e;
            }
        });
    }

    @Transactional
    public void payOrderExecute(Order order, Long point, UserDto userDto, Long shopChannelId, List<Long> coupons) {
        order.getOrderItems().forEach(orderItem -> {
            coupons.forEach(
                    id -> {
                        if (orderItem.getOnsale().getActList().stream().anyMatch(act -> act.getId().equals(id))) {
                            orderItem.setCouponId(id);
                        }
                    }
            );
        });
        order.setPoint(point);
        Long amount = order.getAmount();
        PaymentVo vo = new PaymentVo(LocalDateTime.now().plusMinutes(30), LocalDateTime.now(), "???", amount, 0L, shopChannelId);
        this.createPayment(vo, order.getId(), userDto);
        if (order.getStatus().equals(Order.NEW_ORDER)) {
            switch (order.getOrderItems().get(0).getOnsale().getType()) {
                case Onsale.GROUPON:
                    order.setStatus(Order.GROUP_ON);
                    break;
                case Onsale.ADVANCE_SALE:
                    order.setStatus(Order.ADVANCE_SALE);
                    break;
                default:
                    order.setStatus(Order.PAYED);
            }
        }
        this.updateOrder(order, userDto);
        order.getOrderItems().forEach(orderItem -> {
            this.updateOrderItem(orderItem, userDto);
        });
    }

    @Transactional
    public void createPayment(PaymentVo vo, Long orderId, UserDto userDto) {
        InternalReturnObject<IdDto> retObj = this.paymentService.createPayment(vo);
        if (retObj.getErrno() != ReturnNo.OK.getErrNo()) {
            throw new BusinessException(ReturnNo.getReturnNoByCode(retObj.getErrno()), retObj.getErrmsg());
        }
        OrderPayment orderPayment = new OrderPayment(retObj.getData().getId(), orderId);
        this.orderPaymentDao.insert(orderPayment, userDto);
    }

    /**
     * 创建退款订单
     *
     * @param order   订单
     * @param userDto 登录用户
     */
    @Transactional
    public void createRefund(Order order, UserDto userDto) {
        order.getOrderPayments().forEach(orderPayment -> {
            InternalReturnObject<PaymentDto> retObj = this.paymentService.getPaymentById(PLATFORM, orderPayment.getId());
            if (retObj.getErrno() != ReturnNo.OK.getErrNo()) {
                throw new BusinessException(ReturnNo.getReturnNoByCode(retObj.getErrno()), retObj.getErrmsg());
            }
            PaymentDto paymentDto = retObj.getData();
            RefundVo vo = new RefundVo(paymentDto.getAmount(), paymentDto.getDivAmount());
            InternalReturnObject<IdDto> retObj1 = this.paymentService.createRefund(PLATFORM, orderPayment.getId(), vo);
            if (retObj1.getErrno() != ReturnNo.OK.getErrNo()) {
                throw new BusinessException(ReturnNo.getReturnNoByCode(retObj1.getErrno()), retObj1.getErrmsg());
            }
            Long refundId = retObj1.getData().getId();
            this.orderRefundDao.insert(new OrderRefund(refundId, order.getId()), userDto);
        });
    }

    @Transactional
    public void confirmOrder(Long id, Long shopId, UserDto userDto) {
        logger.debug("confirmOrder: id = {}, shopId = {}, userDto = {}", id, shopId, userDto);
        Order bo = this.orderDao.findById(id);
        if (!shopId.equals(bo.getShopId())) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "订单", id, shopId));
        }
        if (!bo.allowStatus(Order.DELIVERED)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, String.format(ReturnNo.STATENOTALLOW.getMessage(), "订单", id, bo.getStatusName()));
        }
        bo.setStatus(Order.DELIVERED);
        this.updateOrder(bo, userDto);
    }
}
