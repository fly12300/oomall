package cn.edu.xmu.oomall.order.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.order.controller.vo.OrderMessageVo;
import cn.edu.xmu.oomall.order.service.OrderService;
import cn.edu.xmu.oomall.order.service.dto.OrderDto;
import cn.edu.xmu.oomall.order.service.dto.SimpleOrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/shops/{shopId}", produces = "application/json;charset=UTF-8")
public class ShopController {
    private final static Logger logger = LoggerFactory.getLogger(ShopController.class);
    private final OrderService orderService;

    @Autowired
    public ShopController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 商户查询所有名下订单
     *
     * @param shopId     商户id
     * @param customerId 顾客id
     * @param orderSn    订单编号
     * @param beginTime  开始时间
     * @param endTime    结束时间
     * @param page       页码
     * @param pageSize   每页大小
     * @return ReturnObject
     */
    @GetMapping("/orders")
    @Audit(departName = "shops")
    public ReturnObject getOrdersByShopId(@PathVariable Long shopId,
                                          @RequestParam(required = false) Long customerId,
                                          @RequestParam(required = false) String orderSn,
                                          @RequestParam(required = false) LocalDateTime beginTime,
                                          @RequestParam(required = false) LocalDateTime endTime,
                                          @RequestParam(required = false, defaultValue = "1") Integer page,
                                          @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        logger.debug("getOrders: shopId = {}", shopId);
        if (null != beginTime && null != endTime && beginTime.isAfter(endTime)) {
            throw new BusinessException(ReturnNo.LATE_BEGINTIME);
        }
        List<SimpleOrderDto> dtoList = this.orderService.retrieveOrders(shopId, orderSn, null, beginTime, endTime, page, pageSize, customerId);
        return new ReturnObject(new PageDto<>(dtoList, page, pageSize));
    }

    /**
     * 商户修改订单留言
     *
     * @param shopId  商户id
     * @param id      订单id
     * @param vo      订单vo
     * @param userDto 登录用户
     * @return ReturnObject
     */
    @PutMapping("/orders/{id}")
    @Audit(departName = "shops")
    public ReturnObject updateOrderMessage(@PathVariable Long shopId,
                                           @PathVariable Long id,
                                           @Validated @RequestBody OrderMessageVo vo,
                                           @LoginUser UserDto userDto) {
        logger.debug("updateOrderMessage: shopId = {}", shopId);
        this.orderService.updateOrderMessageById(id, shopId, userDto, vo.getMessage());
        return new ReturnObject();
    }

    /**
     * 商户查询店内订单完整信息
     *
     * @param shopId 商户id
     * @param id     订单id
     * @return ReturnObject
     */
    @GetMapping("/orders/{id}")
    @Audit(departName = "shops")
    public ReturnObject getOrderById(@PathVariable Long shopId,
                                     @PathVariable Long id) {
        logger.debug("getOrderById: id = {}, shopId = {}", id, shopId);
        OrderDto dto = this.orderService.findOrderById(id, null, shopId);
        return new ReturnObject(dto);
    }

    /**
     * 店家取消本店铺订单
     *
     * @param shopId  商户id
     * @param id      订单id
     * @param userDto 登录用户
     * @return ReturnObject
     */
    @DeleteMapping("/orders/{id}")
    @Audit(departName = "shops")
    public ReturnObject cancelOrderById(@PathVariable Long shopId,
                                        @PathVariable Long id,
                                        @LoginUser UserDto userDto) {
        logger.debug("cancelOrderById: id = {}, shopId = {}", id, shopId);
        this.orderService.cancelOrderById(id, userDto, true);
        return new ReturnObject();
    }

    /**
     * 商户确认订单
     *
     * @param shopId  商户id
     * @param id      订单id
     * @param userDto 登录用户
     * @return ReturnObject
     */
    @PutMapping("/orders/{id}/confirm")
    @Audit(departName = "shops")
    public ReturnObject confirmOrder(@PathVariable Long shopId,
                                     @PathVariable Long id,
                                     @LoginUser UserDto userDto) {
        logger.debug("confirmOrder: id = {}, shopId = {}", id, shopId);
        this.orderService.confirmOrder(id, shopId, userDto);
        return new ReturnObject();
    }
}
