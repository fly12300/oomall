package cn.edu.xmu.oomall.freight.mapper;

import cn.edu.xmu.oomall.freight.mapper.po.WarehouseRegionPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseRegionPoMapper extends JpaRepository<WarehouseRegionPo,Long> {
    Page<WarehouseRegionPo> findByRegionId(Long regionId, Pageable pageable);

    Optional<WarehouseRegionPo> findByRegionIdAndWarehouseId(Long regionId, Long wid);

    Page<WarehouseRegionPo> findByWarehouseId(Long wid, Pageable pageable);
}
