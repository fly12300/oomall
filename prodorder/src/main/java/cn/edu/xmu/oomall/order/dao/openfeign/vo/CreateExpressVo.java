package cn.edu.xmu.oomall.order.dao.openfeign.vo;

import cn.edu.xmu.oomall.order.controller.vo.ConsigneeVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateExpressVo {
    private Long shopLogisticsId;
    private ConsigneeVo sender;
    private ConsigneeVo delivery;
}
