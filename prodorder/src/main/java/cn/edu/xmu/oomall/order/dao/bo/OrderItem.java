//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.dao.bo;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.order.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.order.dao.openfeign.bo.Coupon;
import cn.edu.xmu.oomall.order.dao.openfeign.bo.Onsale;
import cn.edu.xmu.oomall.order.dao.openfeign.bo.Product;
import cn.edu.xmu.oomall.order.service.dto.IdNameTypeDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;

@ToString(callSuper = true)
@NoArgsConstructor
public class OrderItem extends OOMallObject implements Serializable {

    @Setter
    @Getter
    private Long orderId;
    @Setter
    @Getter
    private Long onsaleId;
    @JsonIgnore
    private Onsale onsale;
    @JsonIgnore
    @ToString.Exclude
    private Product product;
    @JsonIgnore
    @ToString.Exclude
    @Setter
    private GoodsDao goodsDao;
    @Setter
    @Getter
    private Integer quantity;
    @Setter
    @Getter
    private Long price;
    @Setter
    @Getter
    private Long discountPrice;
    @Setter
    @Getter
    private Long point;
    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private Long activityId;
    @JsonIgnore
    @ToString.Exclude
    private IdNameTypeDto activity;
    @Setter
    @Getter
    private Long couponId;
    @JsonIgnore
    private Coupon coupon;
    @Setter
    @Getter
    private Byte commented;

    @Builder
    public OrderItem(Long id, Long creatorId, String creatorName, Long modifierId, String modifierName, LocalDateTime gmtCreate, LocalDateTime gmtModified, Long orderId, Long onsaleId, Integer quantity, Long price, Long discountPrice, Long point, String name, Long activityId, Long couponId, Byte commented) {
        super(id, creatorId, creatorName, modifierId, modifierName, gmtCreate, gmtModified);
        this.orderId = orderId;
        this.onsaleId = onsaleId;
        this.quantity = quantity;
        this.price = price;
        this.discountPrice = discountPrice;
        this.point = point;
        this.name = name;
        this.activityId = activityId;
        this.couponId = couponId;
        this.commented = commented;
    }

    public Onsale getOnsale() {
        if (null == onsale && null != goodsDao) {
            onsale = goodsDao.getOnsaleById(PLATFORM, onsaleId).getData();
        }
        return onsale;
    }

    public Product getProduct() {
        if (null == product && null != goodsDao && null != getOnsale()) {
            InternalReturnObject<Product> retObj = goodsDao.getProductById(PLATFORM, getOnsale().getProduct().getId());
            if (retObj.getErrno() != ReturnNo.OK.getErrNo()) {
                throw new BusinessException(ReturnNo.getReturnNoByCode(retObj.getErrno()), retObj.getErrmsg());
            }
            product = retObj.getData();
        }
        return product;
    }

    public IdNameTypeDto getActivity() {
        if (null == activity && null != activityId && null != getOnsale()) {
            Optional<IdNameTypeDto> onsaleOpt = this.getOnsale().getActList().stream().filter(act -> act.getId().equals(this.activityId)).findAny();
            onsaleOpt.ifPresent(act -> this.activity = act);
        }
        return activity;
    }

    public Coupon getCoupon() {
        if (null == coupon && null != goodsDao && null != couponId && null != this.getActivity() && 0 == this.getActivity().getType()) {
            coupon = goodsDao.getCouponById(PLATFORM, couponId).getData();
        }
        return coupon;
    }
}
