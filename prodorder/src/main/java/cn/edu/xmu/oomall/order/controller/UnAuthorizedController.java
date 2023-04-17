package cn.edu.xmu.oomall.order.controller;

import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.oomall.order.dao.bo.Order;
import cn.edu.xmu.oomall.order.service.dto.StatusDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(produces = "application/json;charset=UTF-8")
public class UnAuthorizedController {
    private final Logger logger = LoggerFactory.getLogger(UnAuthorizedController.class);

    /**
     * 获取订单的所有状态
     *
     * @return ReturnObject
     */
    @GetMapping("/orders/states")
    public ReturnObject getOrderStates() {
        List<StatusDto> dtoList = Order.STATUSNAMES.keySet()
                .stream()
                .map(key -> new StatusDto(key, Order.STATUSNAMES.get(key)))
                .collect(Collectors.toList());
        logger.debug("getOrderStates: states = {}", dtoList);
        return new ReturnObject(ReturnNo.OK, dtoList);
    }
}
