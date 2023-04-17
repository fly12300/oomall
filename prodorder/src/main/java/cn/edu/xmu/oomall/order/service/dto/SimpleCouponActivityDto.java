package cn.edu.xmu.oomall.order.service.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleCouponActivityDto {
    private Long id;
    private String name;
    private Integer quantity;
    private LocalDateTime couponTime;
}
