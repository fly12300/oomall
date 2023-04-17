package cn.edu.xmu.oomall.order.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemDto {
    private Long productId;
    private Long orderId;
    private String name;
    private Integer quantity;
    private Long price;
    private Long discountPrice;
    private IdNameTypeDto activity;
    private SimpleCouponDto coupon;
}
