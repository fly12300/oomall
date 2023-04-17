//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.order.controller.vo.ConsigneeVo;
import cn.edu.xmu.oomall.order.controller.vo.OrderVo;
import cn.edu.xmu.oomall.order.controller.vo.PayInfoVo;
import cn.edu.xmu.oomall.order.service.OrderService;
import cn.edu.xmu.oomall.order.service.dto.ConsigneeDto;
import cn.edu.xmu.oomall.order.service.dto.OrderDto;
import cn.edu.xmu.oomall.order.service.dto.SimpleOrderDto;
import cn.edu.xmu.oomall.order.service.dto.SimpleOrderItemDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

@RestController /*Restful的Controller对象*/
@RequestMapping(produces = "application/json;charset=UTF-8")
public class CustomerController {

    private final static Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private OrderService orderService;

    @Autowired
    public CustomerController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 买家申请建立订单
     *
     * @param orderVo 订单vo
     * @param user    登录用户
     * @return ReturnObject
     */
    @PostMapping("/orders")
    public ReturnObject createOrder(@RequestBody @Validated OrderVo orderVo, @LoginUser UserDto user) {
        orderService.createOrder(orderVo.getOrderItems().stream().map(item -> SimpleOrderItemDto.builder().onsaleId(item.getOnsaleId()).quantity(item.getQuantity()).actId(item.getActId()).build()).collect(Collectors.toList()),
                cloneObj(orderVo.getConsignee(), ConsigneeDto.class),
                orderVo.getMessage(), user);
        return new ReturnObject(ReturnNo.CREATED);
    }

    /**
     * 买家查询名下订单
     *
     * @param orderSn   订单编号
     * @param status    订单状态 100 200 300 400
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @param page      页码
     * @param pageSize  每页数目
     * @param userDto   用户
     * @return ReturnObject
     */
    @GetMapping("/orders")
    @Audit
    public ReturnObject getOrdersByCustomerId(@RequestParam(required = false) String orderSn,
                                              @RequestParam(required = false) Integer status,
                                              @RequestParam(required = false) LocalDateTime beginTime,
                                              @RequestParam(required = false) LocalDateTime endTime,
                                              @RequestParam(required = false, defaultValue = "1") Integer page,
                                              @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                              @LoginUser UserDto userDto) {
        logger.debug("getOrders: userDto = {}", userDto);
        if (null != status && !status.equals(100) && !status.equals(200) && !status.equals(300) && !status.equals(400)) {
            throw new BusinessException(ReturnNo.FIELD_NOTVALID, String.format(ReturnNo.FIELD_NOTVALID.getMessage(), "status"));
        }
        if (null != beginTime && null != endTime && beginTime.isAfter(endTime)) {
            throw new BusinessException(ReturnNo.LATE_BEGINTIME);
        }
        List<SimpleOrderDto> dtoList = this.orderService.retrieveOrders(null, orderSn, status, beginTime, endTime, page, pageSize, userDto.getId());
        return new ReturnObject(new PageDto<>(dtoList, page, pageSize));
    }

    /**
     * 买家查询订单完整信息
     *
     * @param id      订单id
     * @param userDto 登录用户
     * @return ReturnObject
     */
    @GetMapping("/orders/{id}")
    @Audit
    public ReturnObject getOrderById(@PathVariable Long id, @LoginUser UserDto userDto) {
        OrderDto dto = this.orderService.findOrderById(id, userDto.getId(), null);
        return new ReturnObject(dto);
    }

    /**
     * 买家修改本人名下订单
     *
     * @param id      订单id
     * @param vo      订单vo
     * @param userDto 登录用户
     * @return ReturnObject
     */
    @PutMapping("/orders/{id}")
    @Audit
    public ReturnObject updateOrderById(@PathVariable Long id, @RequestBody ConsigneeVo vo, @LoginUser UserDto userDto) {
        this.orderService.updateOrderConsigneeById(id, userDto, vo.getName(), vo.getRegionId(), vo.getAddress(), vo.getMobile());
        return new ReturnObject();
    }

    /**
     * 买家取消本人名下订单
     *
     * @param id      订单id
     * @param userDto 登录用户
     * @return ReturnObject
     */
    @DeleteMapping("/orders/{id}")
    @Audit
    public ReturnObject cancelOrderById(@PathVariable Long id, @LoginUser UserDto userDto) {
        this.orderService.cancelOrderById(id, userDto, false);
        return new ReturnObject();
    }

    /**
     * 买家支付订单
     *
     * @param id      订单id
     * @param userDto 登录用户
     * @param vo      支付信息
     * @return ReturnObject
     */
    @PostMapping("/orders/{id}/pay")
    @Audit
    public ReturnObject payOrder(@PathVariable Long id, @LoginUser UserDto userDto,
                                 @Validated @RequestBody PayInfoVo vo) {
        this.orderService.payOrder(id, userDto, vo.getPoint(), vo.getShopChannel(), vo.getCoupons());
        return new ReturnObject();
    }
}
