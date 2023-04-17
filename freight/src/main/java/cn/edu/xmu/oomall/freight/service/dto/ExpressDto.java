package cn.edu.xmu.oomall.freight.service.dto;

import cn.edu.xmu.oomall.freight.dao.bo.Consignee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpressDto {
    private Long id;
    private String billCode;
    private SimpleLogisticsDto logistics;
    private RouteDto route;
    private Consignee shipper;
    private Consignee receiver;
    private Byte status;
    private SimpleAdminUserDto creator;
    private SimpleAdminUserDto modifier;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;


}
