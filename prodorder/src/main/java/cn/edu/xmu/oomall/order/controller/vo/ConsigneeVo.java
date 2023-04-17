package cn.edu.xmu.oomall.order.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsigneeVo {
    @NotBlank(message = "联系人不能为空")
    private String name;

    @NotBlank(message = "地址不能为空")
    private String address;

    @NotNull(message = "地区不能为空")
    private Long regionId;

    @NotBlank(message = "联系电话不能为空")
    private String mobile;
}
