package cn.edu.xmu.oomall.freight.dao.openfeign;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.oomall.freight.dao.bo.Region;
import cn.edu.xmu.oomall.freight.service.dto.IdNameDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("region-service")
public interface RegionDao {
    @GetMapping("/regions/{id}")
    InternalReturnObject<Region> getRegionById(@PathVariable Long id);
    @GetMapping("/internal/regions/{id}/parents")
    InternalReturnObject<List<IdNameDto>> getParentsRegions(@PathVariable Long id);

}
