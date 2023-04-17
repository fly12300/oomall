package cn.edu.xmu.oomall.freight.mapper;

import cn.edu.xmu.oomall.freight.mapper.po.ShopLogisticsPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopLogisticsPoMapper extends JpaRepository<ShopLogisticsPo, Long> {
    Page<ShopLogisticsPo> findByShopId(Long ShopId, Pageable pageable);

    List<ShopLogisticsPo> findByLogisticsIdAndSecretAndPriority(Long logisticsId, String secret, Integer priority);
}
