package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.WarehouseDao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarehouseLogistics extends OOMallObject implements Serializable {
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
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private Long warehouseId;
    @Getter
    @Setter
    private Long shopLogisticsId;
    @Getter
    @Setter
    private LocalDateTime beginTime;
    @Getter
    @Setter
    private LocalDateTime endTime;
    @Getter
    @Setter
    private Byte invalid;
    @JsonIgnore
    @ToString.Exclude
    private Warehouse warehouse;
    @JsonIgnore
    @ToString.Exclude
    private ShopLogistics shopLogistics;
    @JsonIgnore
    @ToString.Exclude
    @Setter
    private WarehouseDao warehouseDao;
    @JsonIgnore
    @ToString.Exclude
    @Setter
    private ShopLogisticsDao shopLogisticsDao;

    public Warehouse getWarehouse() {
        if(null==this.warehouse&&null!=this.warehouseDao){
            this.warehouse=this.warehouseDao.findById(this.warehouseId);
        }
        return this.warehouse;
    }

    public ShopLogistics getShopLogistics() {
        if(null==this.warehouse&&null!=this.shopLogisticsDao){
            this.shopLogistics=this.shopLogisticsDao.findById(this.shopLogisticsId);
        }
        return this.shopLogistics;
    }
}
