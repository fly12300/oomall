package cn.edu.xmu.oomall.freight.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusVo {

    @NotNull(message = "状态不能为空")
    @Min(value = 0)
    @Max(value = 1)
    private Byte status;

}
