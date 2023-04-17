//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.dao.bo;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.order.dao.OrderItemDao;
import cn.edu.xmu.oomall.order.dao.OrderPaymentDao;
import cn.edu.xmu.oomall.order.dao.openfeign.CustomerDao;
import cn.edu.xmu.oomall.order.dao.openfeign.ExpressDao;
import cn.edu.xmu.oomall.order.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.order.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.order.dao.openfeign.bo.Customer;
import cn.edu.xmu.oomall.order.dao.openfeign.bo.Express;
import cn.edu.xmu.oomall.order.dao.openfeign.bo.FreightPrice;
import cn.edu.xmu.oomall.order.dao.openfeign.bo.Shop;
import cn.edu.xmu.oomall.order.dao.openfeign.vo.FreightCalcVo;
import cn.edu.xmu.oomall.order.service.openfeign.dto.DiscountDto;
import cn.edu.xmu.oomall.order.service.openfeign.vo.SimpleOrderItemVo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;

@ToString(callSuper = true)
@NoArgsConstructor
public class Order extends OOMallObject implements Serializable {

    /**
     * 新订单
     */
    @JsonIgnore
    public static final Integer NEW_ORDER = 101;
    /**
     * 待支付尾款
     */
    @JsonIgnore
    public static final Integer ADVANCE_SALE = 102;
    /**
     * 已付款
     */
    @JsonIgnore
    public static final Integer PAYED = 201;
    /**
     * 待成团
     */
    @JsonIgnore
    public static final Integer GROUP_ON = 202;
    /**
     * 待发货
     */
    @JsonIgnore
    public static final Integer UNDELIVERED = 203;
    /**
     * 已发货
     */
    @JsonIgnore
    public static final Integer DELIVERED = 204;
    /**
     * 已完成
     */
    @JsonIgnore
    public static final Integer FINISHED = 300;
    /**
     * 待退款
     */
    @JsonIgnore
    public static final Integer REFUNDING = 401;
    /**
     * 已取消
     */
    @JsonIgnore
    public static final Integer CANCELED = 402;
    @ToString.Exclude
    @JsonIgnore
    public static final Map<Integer, String> STATUSNAMES = new HashMap<>() {
        {
            put(NEW_ORDER, "新订单");
            put(ADVANCE_SALE, "待支付尾款");
            put(PAYED, "已付款");
            put(UNDELIVERED, "待发货");
            put(GROUP_ON, "待成团");
            put(DELIVERED, "已发货");
            put(FINISHED, "已完成");
            put(REFUNDING, "待退款");
            put(CANCELED, "已取消");
        }
    };
    /**
     * 允许的状态迁移
     */
    @JsonIgnore
    @ToString.Exclude
    private static final Map<Integer, Set<Integer>> toStatus = new HashMap<>() {
        {
            put(NEW_ORDER, new HashSet<>() {
                {
                    add(ADVANCE_SALE);
                    add(GROUP_ON);
                    add(PAYED);
                }
            });
            put(ADVANCE_SALE, new HashSet<>() {
                {
                    add(PAYED);
                    add(REFUNDING);
                    add(CANCELED);
                }
            });
            put(PAYED, new HashSet<>() {
                {
                    add(UNDELIVERED);
                    add(FINISHED);
                    add(REFUNDING);
                }
            });
            put(GROUP_ON, new HashSet<>() {
                {
                    add(PAYED);
                    add(REFUNDING);
                }
            });
            put(UNDELIVERED, new HashSet<>() {
                {
                    add(DELIVERED);
                    add(REFUNDING);
                }
            });
            put(DELIVERED, new HashSet<>() {
                {
                    add(FINISHED);
                    add(REFUNDING);
                }
            });
            put(REFUNDING, new HashSet<>() {
                {
                    add(CANCELED);
                }
            });
        }
    };
    @Setter
    @Getter
    private Integer status;
    @Setter
    @Getter
    private Long customerId;
    @JsonIgnore
    private Customer customer;
    @ToString.Exclude
    @JsonIgnore
    @Setter
    private CustomerDao customerDao;


    @Setter
    @Getter
    private Long shopId;
    @JsonIgnore
    private Shop shop;
    @ToString.Exclude
    @JsonIgnore
    @Setter
    private ShopDao shopDao;
    @Setter
    @Getter
    private String orderSn;
    @Setter
    @Getter
    private Long pid;
    @Setter
    @Getter
    private String consignee;
    @Setter
    @Getter
    private Long regionId;
    @Setter
    @Getter
    private String address;
    @Setter
    @Getter
    private String mobile;
    @Setter
    @Getter
    private String message;
    @Setter
    private Long expressFee;

    /**
     * 计算运费
     *
     * @return 运费
     */
    private Long getExpressFee() {
        if (null == expressFee && null != goodsDao && null != shopDao) {
            expressFee = 0L;
            orderItems.stream()
                    .collect(Collectors.groupingBy(orderItem -> orderItem.getProduct().getTemplate().getId()))
                    .forEach((templateId, orderItemList) -> {
                        List<FreightCalcVo> voList = orderItemList.stream()
                                .map(orderItem -> new FreightCalcVo(orderItem.getId(), orderItem.getProduct().getId(), orderItem.getQuantity(), orderItem.getProduct().getWeight()))
                                .collect(Collectors.toList());
                        InternalReturnObject<FreightPrice> retObj = shopDao.calculateFreightPrice(templateId, this.getRegionId(), voList);
                        if (retObj.getErrno() != ReturnNo.OK.getErrNo()) {
                            throw new BusinessException(ReturnNo.getReturnNoByCode(retObj.getErrno()), retObj.getErrmsg());
                        }
                        expressFee += retObj.getData().getFreightPrice();
                    });
        }
        return expressFee;
    }

    @Setter
    @Getter
    private Long packageId;
    @JsonIgnore
    private Express pack;
    @ToString.Exclude
    @JsonIgnore
    @Setter
    private ExpressDao expressDao;
    @Setter
    private Long discountPrice;

    /**
     * 获取折扣价格
     */
    private Long getDiscountPrice() {
        if ((null == discountPrice || 0 == discountPrice) && null != goodsDao && null != this.getOrderItems()) {
            Map<Long, List<OrderItem>> orderItemsMap = this.getOrderItems()
                    .stream()
                    .filter(orderItem -> orderItem.getCouponId() != null)
                    .collect(Collectors.groupingBy(OrderItem::getCouponId));
            orderItemsMap.keySet().forEach(couponId -> {
                List<SimpleOrderItemVo> voList = orderItemsMap.get(couponId).stream().map(orderItem -> new SimpleOrderItemVo(orderItem.getOnsaleId(), orderItem.getQuantity())).collect(Collectors.toList());
                InternalReturnObject<List<DiscountDto>> retObj = goodsDao.calculateDiscount(couponId, voList);
                if (retObj.getErrno() != ReturnNo.OK.getErrNo()) {
                    throw new BusinessException(ReturnNo.getReturnNoByCode(retObj.getErrno()), retObj.getErrmsg());
                }
                retObj.getData()
                        .forEach(discountDto -> {
                            Optional<OrderItem> itemOpt = this.getOrderItems().stream().filter(orderItem -> orderItem.getOnsaleId().equals(discountDto.getId())).findFirst();
                            itemOpt.ifPresent(orderItem -> {
                                orderItem.setDiscountPrice(discountDto.getDiscount());
                                this.discountPrice += discountDto.getDiscount();
                            });
                        });
            });
        }
        return discountPrice;
    }

    @Setter
    private Long originPrice;

    private Long getOriginPrice() {
        if (null == originPrice && null != this.getOrderItems()) {
            originPrice = this.getOrderItems().stream().mapToLong(orderItems -> orderItems.getPrice() * orderItems.getQuantity()).sum();
        }
        return originPrice;
    }

    @Getter
    @Setter
    private Long point;
    @Setter
    @JsonIgnore
    private List<OrderItem> orderItems;
    @ToString.Exclude
    @JsonIgnore
    @Setter
    private OrderItemDao orderItemDao;
    @JsonIgnore
    private List<OrderPayment> orderPayments;
    @Setter
    @JsonIgnore
    @ToString.Exclude
    private OrderPaymentDao orderPaymentDao;

    public List<OrderPayment> getOrderPayments() {
        if (null == orderPayments && null != orderPaymentDao) {
            orderPayments = orderPaymentDao.retrieveByOrderId(id);
        }
        return orderPayments;
    }

    @JsonIgnore
    @ToString.Exclude
    private Long amount;
    @JsonIgnore
    @ToString.Exclude
    @Setter
    private GoodsDao goodsDao;

    //计算订单支付费用
    public Long getAmount() {
        if (null == amount) {
            amount = getOriginPrice() - getDiscountPrice() - point + getExpressFee();
        }
        return amount;
    }

    @Builder
    public Order(Long id, Long creatorId, String creatorName, Long modifierId, String modifierName, LocalDateTime gmtCreate, LocalDateTime gmtModified, Long customerId, Long shopId, String orderSn, Long pid, String consignee, Long regionId, String address, String mobile, String message, Long packageId, List<OrderItem> orderItems) {
        super(id, creatorId, creatorName, modifierId, modifierName, gmtCreate, gmtModified);
        this.customerId = customerId;
        this.shopId = shopId;
        this.orderSn = orderSn;
        this.pid = pid;
        this.consignee = consignee;
        this.regionId = regionId;
        this.address = address;
        this.mobile = mobile;
        this.message = message;
        this.packageId = packageId;
        this.orderItems = orderItems;
    }

    /**
     * 是否允许状态迁移
     *
     * @param status
     * @return
     * @author Ming Qiu
     * <p>
     * date: 2022-11-13 0:25
     */
    public boolean allowStatus(Integer status) {
        boolean ret = false;

        if (null != status && null != this.status) {
            Set<Integer> allowStatusSet = toStatus.get(this.status);
            if (null != allowStatusSet) {
                ret = allowStatusSet.contains(status);
            }
        }
        return ret;
    }

    /**
     * 获得当前状态名称
     *
     * @return
     * @author Ming Qiu
     * <p>
     * date: 2022-11-13 0:43
     */
    @JsonIgnore
    public String getStatusName() {
        return STATUSNAMES.get(this.status);
    }

    public Customer getCustomer() {
        if (null == customer && null != customerDao) {
            customer = customerDao.getCustomerById(PLATFORM, customerId).getData();
        }
        return customer;
    }

    public Shop getShop() {
        if (null == shop && null != shopDao) {
            shop = shopDao.getShopById(shopId).getData();
        }
        return shop;
    }

    public Express getPack() {
        if (null == pack && null != expressDao && null != packageId) {
            pack = expressDao.getExpressById(packageId).getData();
        }
        return pack;
    }

    public List<OrderItem> getOrderItems() {
        if (null == orderItems && null != orderItemDao) {
            orderItems = orderItemDao.retrieveByOrderId(id);
        }
        return orderItems;
    }
}
