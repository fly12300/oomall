package cn.edu.xmu.oomall.order.mapper;

import cn.edu.xmu.oomall.order.mapper.po.OrderRefundPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRefundPoMapper extends JpaRepository<OrderRefundPo, Long> {
    Page<OrderRefundPo> findByOrderId(Long orderId, Pageable pageable);
}
