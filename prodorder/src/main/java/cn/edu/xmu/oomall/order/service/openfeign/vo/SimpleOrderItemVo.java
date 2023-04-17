package cn.edu.xmu.oomall.order.service.openfeign.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleOrderItemVo {
    /**
     * onsaleId
     */
    private Long id;
    private Integer quantity;
}
