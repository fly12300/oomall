package cn.edu.xmu.oomall.order.dao.openfeign.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreightCalcVo {
    private Long orderItemId;
    private Long productId;
    private Integer quantity;
    private Integer weight;
}
