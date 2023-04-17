package cn.edu.xmu.oomall.freight.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopLogisticsVo {
    @NotBlank(message = "密钥不能为空")
    private String secret;
    @NotNull(message = "优先级不能为空")
    private Integer priority;
}
