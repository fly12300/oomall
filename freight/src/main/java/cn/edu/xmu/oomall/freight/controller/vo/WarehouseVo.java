package cn.edu.xmu.oomall.freight.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseVo {
    @NotBlank(message = "仓库名不能为空")
    private String name;
    @NotBlank(message = "详细地址不能为空")
    private String address;
    @Min(value = 0, message = "regionId至少为0")
    private Long regionId;
    @NotBlank(message = "联系人姓名不能为空")
    private String senderName;
    @NotBlank(message = "联系电话不能为空")
    private String senderMobile;
}
