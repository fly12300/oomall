package cn.edu.xmu.oomall.freight.dao.bo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class Consignee {
    @NotBlank(message = "姓名不能为空")
    private String name;
    @NotBlank(message = "电话号码不能为空")
    private String mobile;
    @NotNull(message = "地区id不能为空")
    @Min(value = 0,message = "最小值为0")
    private Long regionId;
    @NotBlank(message = "详细地址不能为空")
    private String address;


}
