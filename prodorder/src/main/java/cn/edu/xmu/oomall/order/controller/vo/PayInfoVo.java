package cn.edu.xmu.oomall.order.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayInfoVo {
    @NotNull(message = "支付积点不能为空")
    private Long point;
    @NotNull(message = "支付渠道不能为空")
    private Long shopChannel;
    private List<Long> coupons;
}
