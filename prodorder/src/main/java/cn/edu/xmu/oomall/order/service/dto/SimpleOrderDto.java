package cn.edu.xmu.oomall.order.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleOrderDto {
    private Long id;
    private Integer status;
    private LocalDateTime gmtCreate;
    private Long originPrice;
    private Long discountPrice;
    private Long freightPrice;
}
