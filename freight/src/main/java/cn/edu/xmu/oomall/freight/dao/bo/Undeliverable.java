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
import java.time.LocalDateTime;

@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Undeliverable extends OOMallObject implements Serializable {
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private Long regionId;
    @Getter
    @Setter
    private Long shopLogisticsId;
    @Getter
    @Setter
    private LocalDateTime beginTime;
    @Getter
    @Setter
    private LocalDateTime endTime;
    @JsonIgnore
    @ToString.Exclude
    private Region region;
    @JsonIgnore
    @ToString.Exclude
    private ShopLogistics shopLogistics;
    @JsonIgnore
    @ToString.Exclude
    @Setter
    private RegionDao regionDao;
    @JsonIgnore
    @ToString.Exclude
    @Setter
    private ShopLogisticsDao shopLogisticsDao;

    public Region getRegion() {
        if (null == this.region && null != this.regionDao) {
            this.region = this.regionDao.getRegionById(regionId).getData();
        }
        return region;
    }

    public ShopLogistics getShopLogistics() {
        //TODO: 用Dao获取
        return shopLogistics;
    }
}
