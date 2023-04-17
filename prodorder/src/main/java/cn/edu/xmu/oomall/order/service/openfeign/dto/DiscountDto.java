package cn.edu.xmu.oomall.order.service.openfeign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountDto {
    private Long id;
    private Integer quantity;
    private Long price;
    private Long discount;
}
