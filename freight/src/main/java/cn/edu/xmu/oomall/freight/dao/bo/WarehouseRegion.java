package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.freight.dao.WarehouseDao;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarehouseRegion extends OOMallObject implements Serializable {
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private Long warehouseId;
    @Getter
    @Setter
    private Long regionId;
    @Getter
    @Setter
    private LocalDateTime beginTime;
    @Getter
    @Setter
    private LocalDateTime endTime;
    @JsonIgnore
    @ToString.Exclude
    private Warehouse warehouse;
    @JsonIgnore
    @ToString.Exclude
    private Region region;
    @JsonIgnore
    @ToString.Exclude
    @Setter
    private RegionDao regionDao;
    @JsonIgnore
    @ToString.Exclude
    @Setter
    private WarehouseDao warehouseDao;

    public Warehouse getWarehouse() {
        if (null == this.warehouse && null != this.warehouseDao) {
            this.warehouse = this.warehouseDao.findById(warehouseId);
        }
        return this.warehouse;
    }

    public Region getRegion() {
        if (null == this.region && null != this.regionDao) {
            this.region = this.regionDao.getRegionById(regionId).getData();
        }
        return this.region;
    }

    public WarehouseRegion(Long warehouseId, Long regionId, LocalDateTime beginTime, LocalDateTime endTime) {
        this.warehouseId = warehouseId;
        this.regionId = regionId;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }
}
