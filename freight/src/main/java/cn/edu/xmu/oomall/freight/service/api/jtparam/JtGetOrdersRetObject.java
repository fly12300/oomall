package cn.edu.xmu.oomall.freight.service.api.jtparam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JtGetOrdersRetObject {
    /**
     * 订单状态：
     * 104 已取消
     * 103 已取件
     * 102 已调派业务员
     * 101 已调派网点
     * 100 未调派
     */
    String orderStatus;
}
