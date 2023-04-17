package cn.edu.xmu.oomall.order.service.openfeign.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentVo {
    private LocalDateTime timeExpire;
    private LocalDateTime timeBegin;
    private String spOpenid;
    private Long amount;
    private Long divAmount;
    private Long shopChannelId;
}
