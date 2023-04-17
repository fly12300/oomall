package cn.edu.xmu.oomall.order.service.openfeign.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundVo {
    private Long amount;
    private Long divAmount;
}
