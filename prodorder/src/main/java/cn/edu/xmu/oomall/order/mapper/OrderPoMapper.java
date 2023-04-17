//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.mapper;

import cn.edu.xmu.oomall.order.mapper.po.OrderPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface OrderPoMapper extends JpaRepository<OrderPo, Long> {
    @Query(value = "select po from OrderPo po where (?1 is null or po.customerId = ?1) " +
            "and (?2 is null or po.orderSn = ?2) " +
            "and (?3 is null or (po.status >= ?3 and po.status < ?3 + 100))" +
            "and (?4 is null or po.gmtCreate >= ?4)" +
            "and (?5 is null or po.gmtCreate <= ?5)" +
            "and (?6 is null or po.shopId = ?6)")
    Page<OrderPo> findOrderPos(Long customerId, String orderSn, Integer status, LocalDateTime beginTime, LocalDateTime endTime, Long shopId, Pageable pageable);
}
