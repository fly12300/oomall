package cn.edu.xmu.oomall.order.service.rocketmq;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.order.dao.bo.Order;
import cn.edu.xmu.oomall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RocketMQMessageListener(topic = "order-pay-topic", consumerGroup = "order-group", consumeThreadMax = 10)
public class PayOrderListener implements RocketMQListener<Map<String, Object>> {
    private final OrderService orderService;

    public PayOrderListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void onMessage(Map<String, Object> msgMap) {
        Order order = (Order) msgMap.get("body");
        Long point = (Long) msgMap.get("point");
        UserDto userDto = (UserDto) msgMap.get("userDto");
        Long shopChannelId = (Long) msgMap.get("shopChannelId");
        List<Long> coupons = (List<Long>) msgMap.get("coupons");
        this.orderService.payOrderExecute(order, point, userDto, shopChannelId, coupons);
    }
}
