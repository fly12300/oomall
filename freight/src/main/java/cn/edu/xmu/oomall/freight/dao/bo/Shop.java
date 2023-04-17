//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 商铺对象
 */
@ToString(callSuper = true)
@NoArgsConstructor
public class Shop extends OOMallObject implements Serializable {

    /**
     * 申请
     */
    @JsonIgnore
    @ToString.Exclude
    public static final Byte NEW = 0;
    /**
     * 下线
     */
    @JsonIgnore
    @ToString.Exclude
    public static final Byte OFFLINE = 1;
    /**
     * 上线
     */
    @JsonIgnore
    @ToString.Exclude
    public static final Byte ONLINE = 2;
    /**
     * 停用
     *
    /**
     * 服务商
     */
    @JsonIgnore
    @ToString.Exclude
    public static final Byte SERVICE = 1;
    /**
     * 电商
     */
    @JsonIgnore
    @ToString.Exclude
    public static final Byte RETAILER = 0;

    /**
     * 商铺名称
     */
    @Setter
    @Getter
    private String name;

    /**
     * 商铺保证金
     */
    @Setter
    @Getter
    private Long deposit;

    /**
     * 商铺保证金门槛
     */
    @Setter
    @Getter
    private Long depositThreshold;

    /**
     * 状态
     */
    @Setter
    @Getter
    private Byte status;

    /**
     * 详细地址
     */
    @Setter
    @Getter
    private String address;

    /**
     * 联系人
     */
    @Setter
    @Getter
    private String consignee;

    /**
     * 电话
     */
    @Setter
    @Getter
    private String mobile;

    /**
     * 免邮门槛
     */
    @Setter
    @Getter
    private Integer freeThreshold;

    /**
     * 类型 0：电商 1：服务商
     */
    @Setter
    @Getter
    private Byte type;


    /**
     * 地区
     */
    @Setter
    @Getter
    private Long regionId;
    @Builder
    public Shop(Long id, Long creatorId, String creatorName, Long modifierId, String modifierName, LocalDateTime gmtCreate, LocalDateTime gmtModified, String name, Long deposit, Long depositThreshold, Byte status, String address, String consignee, String mobile, Byte type, Long regionId, Integer freeThreshold) {
        super(id, creatorId, creatorName, modifierId, modifierName, gmtCreate, gmtModified);
        this.name = name;
        this.deposit = deposit;
        this.depositThreshold = depositThreshold;
        this.status = status;
        this.address = address;
        this.consignee = consignee;
        this.mobile = mobile;
        this.type = type;
        this.regionId = regionId;
        this.freeThreshold = freeThreshold;
    }
}
