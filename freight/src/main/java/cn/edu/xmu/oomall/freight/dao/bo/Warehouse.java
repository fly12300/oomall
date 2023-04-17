package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.freight.dao.WarehouseLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Warehouse extends OOMallObject implements Serializable {
    /**
     * 有效
     */
    @JsonIgnore
    @ToString.Exclude
    public static final Byte VALID = 0;
    /**
     * 无效
     */
    @JsonIgnore
    @ToString.Exclude
    public static final Byte INVALID = 1;

    /**
     * 名称
     */
    @Getter
    @Setter
    private String name;

    /**
     * 详细地址
     */
    @Getter
    @Setter
    private String address;

    /**
     * 所在地区id
     */
    @Getter
    @Setter
    private Long regionId;

    /**
     * 所在地区
     */
    @JsonIgnore
    @ToString.Exclude
    private Region region;

    @Setter
    @JsonIgnore
    @ToString.Exclude
    private RegionDao regionDao;
    /**
     * 联系人
     */
    @Getter
    @Setter
    private String senderName;
    /**
     * 联系电话
     */
    @Getter
    @Setter
    private String senderMobile;
    @Getter
    @Setter
    private Long shopId;
    /**
     * 优先级（小-优先）
     */
    @Getter
    @Setter
    private Integer priority;
    /**
     * 0有效 1无效
     */
    @Getter
    @Setter
    private Byte invalid;

    /**
     * 对应仓库物流
     */
    @JsonIgnore
    @ToString.Exclude
    private List<WarehouseLogistics> warehouseLogisticsList;

    @Setter
    @JsonIgnore
    @ToString.Exclude
    private WarehouseLogisticsDao warehouseLogisticsDao;

    public Warehouse(Long shopId, String name, String address, Long regionId,
                     String senderName, String senderMobile, Byte invalid, Integer priority) {
        this.shopId = shopId;
        this.name = name;
        this.address = address;
        this.regionId = regionId;
        this.senderName = senderName;
        this.senderMobile = senderMobile;
        this.invalid = invalid;
        this.priority = priority;
    }

    public Region getRegion() {
        if (null == this.region && null != this.regionDao) {
            this.region = this.regionDao.getRegionById(this.regionId).getData();
        }
        return this.region;
    }

    public List<WarehouseLogistics> getWarehouseLogistics(Integer page,Integer pageSize){
        if(null==this.warehouseLogisticsList&&null!=this.warehouseLogisticsDao){
            this.warehouseLogisticsList=this.warehouseLogisticsDao.getByWarehouseId(this.id,page,pageSize);
        }
        return this.warehouseLogisticsList;
    }
}
