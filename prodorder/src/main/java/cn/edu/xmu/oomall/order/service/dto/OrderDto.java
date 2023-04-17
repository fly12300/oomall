package cn.edu.xmu.oomall.order.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderDto {
    private Long id;
    private String orderSn;
    private IdNameDto customer;
    private IdNameTypeDto shop;
    private Long pid;
    private Integer status;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private Long originPrice;
    private Long discountPrice;
    private Long expressFee;
    private String message;
    private ConsigneeDto consignee;
    private SimplePackageDto pack;
    private List<OrderItemDto> orderItems;
}
