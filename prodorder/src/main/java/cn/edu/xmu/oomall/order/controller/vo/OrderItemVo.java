//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemVo {

    @NotNull(message = "商品销售不能为空")
    private Long onsaleId;

    @Min(value = 1, message = "至少购买一个")
    private Integer quantity;

    private Long actId;
}

