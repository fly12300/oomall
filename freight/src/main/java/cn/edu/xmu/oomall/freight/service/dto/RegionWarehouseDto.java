package cn.edu.xmu.oomall.freight.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionWarehouseDto {

    private SimpleWarehouseDto warehouse;
    private Byte status;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private SimpleAdminUserDto creator;
    private SimpleAdminUserDto modifier;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

}
