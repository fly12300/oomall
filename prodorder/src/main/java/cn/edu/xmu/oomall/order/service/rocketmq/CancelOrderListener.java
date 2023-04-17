package cn.edu.xmu.oomall.order.service.rocketmq;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.order.dao.bo.Order;
import cn.edu.xmu.oomall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RocketMQMessageListener(topic = "order-cancel-topic", consumerGroup = "order-group", consumeThreadMax = 10)
public class CancelOrderListener implements RocketMQListener<Map<String, Object>> {
    private final OrderService orderService;

    @Autowired
    public CancelOrderListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void onMessage(Map<String, Object> msgMap) {
        log.debug("receive msg: msgMap = {}", msgMap);
        UserDto userDto = (UserDto) msgMap.get("userDto");
        Order order = (Order) msgMap.get("body");
        this.orderService.cancelOrderExecute(order, userDto);
    }
}
