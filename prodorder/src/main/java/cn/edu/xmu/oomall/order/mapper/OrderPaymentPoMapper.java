package cn.edu.xmu.oomall.order.mapper;

import cn.edu.xmu.oomall.order.mapper.po.OrderPaymentPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderPaymentPoMapper extends JpaRepository<OrderPaymentPo, Long> {
    Page<OrderPaymentPo> findByOrderId(Long orderId, Pageable pageable);
}
