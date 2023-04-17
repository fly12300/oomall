package cn.edu.xmu.oomall.order.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessageVo {
    @NotBlank(message = "信息不能为空")
    private String message;
}
