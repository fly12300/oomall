package cn.edu.xmu.oomall.freight.controller.vo;

import cn.edu.xmu.oomall.freight.dao.bo.Consignee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpressVo {
    @Min(0)
    private Long shopLogisticId;
    @NotNull
    private Consignee sender;
    @NotNull
    private Consignee delivery;

}
