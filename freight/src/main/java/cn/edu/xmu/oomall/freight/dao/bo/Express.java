package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Express extends OOMallObject implements Serializable {
    /**
     * 共9种状态
     */
    //未发货
    @ToString.Exclude
    @JsonIgnore
    public static final Byte UNDELIVERED = 0;
    //在途
    @ToString.Exclude
    @JsonIgnore
    public static final Byte DELIVERING = 1;
    //签收
    @ToString.Exclude
    @JsonIgnore
    public static final Byte SIGNED = 2;
    //取消
    @ToString.Exclude
    @JsonIgnore
    public static final Byte CANCELED = 3;
    //拒收
    @ToString.Exclude
    @JsonIgnore
    public static final Byte REJECTED = 4;
    //已退回
    @ToString.Exclude
    @JsonIgnore
    public static final Byte RETURNING = 5;
    //丢失
    @ToString.Exclude
    @JsonIgnore
    public static final Byte LOST = 6;
    //回收
    @ToString.Exclude
    @JsonIgnore
    public static final Byte RETURNED = 7;
    //破损
    @ToString.Exclude
    @JsonIgnore
    public static final Byte BROKEN = 8;

    @ToString.Exclude
    @JsonIgnore
    public static final Map<Byte, String> STATUSNAMES = new HashMap<>() {
        {
            put(UNDELIVERED, "未发货");
            put(DELIVERING, "在途");
            put(SIGNED, "签收");
            put(CANCELED, "取消");
            put(REJECTED, "拒收");
            put(RETURNING, "已退回");
            put(LOST, "丢失");
            put(RETURNED, "回收");
            put(BROKEN, "破损");
        }
    };

    /**
     * 允许的状态迁移
     */
    @JsonIgnore
    @ToString.Exclude
    private static final Map<Byte, Set<Byte>> toStatus = new HashMap<>() {
        {
            put(UNDELIVERED, new HashSet<>() {
                {
                    add(DELIVERING);
                    add(CANCELED);
                }
            });
            put(DELIVERING, new HashSet<>() {
                {
                    add(SIGNED);
                    add(REJECTED);
                    add(LOST);
                }
            });
            put(REJECTED, new HashSet<>() {
                {
                    add(LOST);
                    add(RETURNING);
                }
            });
            put(RETURNING, new HashSet<>() {
                {
                    add(RETURNED);
                    add(BROKEN);
                }
            });
        }
    };
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private String billCode;
    @Getter
    @Setter
    private Long shopLogisticsId;
    @Getter
    @Setter
    private Long senderRegionId;
    @Getter
    @Setter
    private String senderAddress;
    @Getter
    @Setter
    private Long deliveryRegionId;
    @Getter
    @Setter
    private String deliverAddress;
    @Getter
    @Setter
    private String senderName;
    @Getter
    @Setter
    private String senderMobile;
    @Getter
    @Setter
    private String deliveryName;
    @Getter
    @Setter
    private String deliverMobile;
    @Getter
    @Setter
    private Byte status;
    @Getter
    @Setter
    private Long shopId;
    @JsonIgnore
    @ToString.Exclude
    private ShopLogistics shopLogistics;
    @Setter
    @JsonIgnore
    @ToString.Exclude
    private Logistics logistics;
    @JsonIgnore
    @ToString.Exclude
    private Region senderRegion;
    @JsonIgnore
    @ToString.Exclude
    private Region deliveryRegion;
    @JsonIgnore
    @ToString.Exclude
    @Setter
    private ShopLogisticsDao shopLogisticsDao;
    @JsonIgnore
    @ToString.Exclude
    @Setter
    private RegionDao regionDao;

    /**
     * 是否允许状态迁移
     */
    public boolean allowStatus(Byte status) {
        boolean ret = false;

        if (null != status && null != this.status) {
            Set<Byte> allowStatusSet = toStatus.get(this.status);
            if (null != allowStatusSet) {
                ret = allowStatusSet.contains(status);
            }
        }
        return ret;
    }

    /**
     * 获得当前状态名称
     */
    @JsonIgnore
    public String getStatusName() {
        return STATUSNAMES.get(this.status);
    }

    public ShopLogistics getShopLogistics() {
        if (null == this.shopLogistics && null != this.shopLogisticsDao) {
            this.shopLogistics = this.shopLogisticsDao.findById(this.shopLogisticsId);
        }
        System.out.println(shopLogistics);
        return this.shopLogistics;
    }

    public Logistics getLogistics() {
        if (null == this.logistics && null != this.getShopLogistics()) {
            this.logistics = this.getShopLogistics().getLogistics();
        }
        return this.logistics;
    }



    public Region getSenderRegion() {
        if (null == this.senderRegion && null != this.regionDao) {
            this.senderRegion = this.regionDao.getRegionById(senderRegionId).getData();
        }
        return this.senderRegion;
    }

    public Region getDeliveryRegion() {
        if (null == this.deliveryRegion && null != this.regionDao) {
            this.deliveryRegion = this.regionDao.getRegionById(deliveryRegionId).getData();
        }
        return this.deliveryRegion;
    }

    public Express(String billCode, Long senderRegionId, String senderAddress,
                   Long deliveryRegionId, String deliverAddress, String senderName,
                   String senderMobile, String deliveryName, String deliverMobile,
                   Byte status, Long shopId,Long shopLogisticsId) {
        this.billCode = billCode;
        this.senderRegionId = senderRegionId;
        this.senderAddress = senderAddress;
        this.deliveryRegionId = deliveryRegionId;
        this.deliverAddress = deliverAddress;
        this.senderName = senderName;
        this.senderMobile = senderMobile;
        this.deliveryName = deliveryName;
        this.deliverMobile = deliverMobile;
        this.status = status;
        this.shopId = shopId;
        this.shopLogisticsId = shopLogisticsId;
    }
}
