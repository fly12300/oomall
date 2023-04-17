package cn.edu.xmu.oomall.order.dao.openfeign.bo;

import cn.edu.xmu.oomall.order.service.dto.IdNameTypeDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {
    private Long id;
    private String name;
    private IdNameTypeDto shop;
    private Integer quantity;
    private Byte quantityType;
    private Integer validTerm;
    private LocalDateTime couponTime;
    private String strategy;
}
