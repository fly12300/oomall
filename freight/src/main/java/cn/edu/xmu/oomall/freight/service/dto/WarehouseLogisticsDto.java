package cn.edu.xmu.oomall.freight.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseLogisticsDto {
    private ShopLogisticsDto shopLogistics;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Integer status;
    private IdNameDto creator;
    private IdNameDto modifier;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
