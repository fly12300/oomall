package cn.edu.xmu.oomall.order.service.openfeign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentDto {
    private Long id;
    private Long amount;
    private Long divAmount;
}
