package cn.edu.xmu.oomall.freight.mapper;


import cn.edu.xmu.oomall.freight.dao.bo.WarehouseLogistics;
import cn.edu.xmu.oomall.freight.mapper.po.UndeliverablePo;
import cn.edu.xmu.oomall.freight.mapper.po.WarehouseLogisticsPo;
import com.github.pagehelper.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseLogisticsPoMapper extends JpaRepository<WarehouseLogisticsPo, Long> {
    Page<WarehouseLogisticsPo> findByWarehouseId(Long warehouseId, Pageable pageable);
}
