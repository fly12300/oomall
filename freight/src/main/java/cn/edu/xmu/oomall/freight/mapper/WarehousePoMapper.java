package cn.edu.xmu.oomall.freight.mapper;

import cn.edu.xmu.oomall.freight.mapper.po.WarehousePo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehousePoMapper extends JpaRepository<WarehousePo, Long> {
    Page<WarehousePo> findByShopId(Long shopId, Pageable pageable);
}
