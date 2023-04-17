package cn.edu.xmu.oomall.freight.service.api;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.oomall.freight.service.api.jtparam.*;
import org.springframework.stereotype.Service;

//TODO: 这部分返回的内容要符合测试用例的要求

/**
 * 模拟极兔api服务
 */
@Service
public class JtService {
    /**
     * 极兔api创建订单
     */
    public InternalReturnObject<JtAddOrderRetObject> addOrder(JtAddOrderParam param) {
        JtAddOrderRetObject ret = new JtAddOrderRetObject("JT0000498364212");
        return new InternalReturnObject<>(ret);
    }

    /**
     * 极兔api查询订单
     */
    public InternalReturnObject<JtGetOrdersRetObject> getOrders(JtBillCodeParam param) {
        JtGetOrdersRetObject ret = new JtGetOrdersRetObject("100");
        return new InternalReturnObject<>(ret);
    }

    /**
     * 极兔api查询物流
     */
    public InternalReturnObject<JtLogisticsTraceRetObject> logisticsTrace(JtBillCodeParam param) {
        JtLogisticsTraceRetObject ret = new JtLogisticsTraceRetObject("物流信息");
        return new InternalReturnObject<>(ret);
    }
}
