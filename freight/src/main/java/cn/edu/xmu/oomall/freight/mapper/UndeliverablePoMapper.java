package cn.edu.xmu.oomall.freight.mapper;

import cn.edu.xmu.oomall.freight.mapper.po.ShopLogisticsPo;
import cn.edu.xmu.oomall.freight.mapper.po.UndeliverablePo;
import com.github.pagehelper.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UndeliverablePoMapper extends JpaRepository<UndeliverablePo, Long> {
    Page<UndeliverablePo> findByShopLogisticsId(Long shopLogisticId, Pageable pageable);
}
