package cn.edu.xmu.oomall.freight.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class WarehouseDto {
    private Long id;
    private String name;
    private IdNameDto region;
    private String senderName;
    private String senderMobile;
    private Byte status;
    private Integer priority;
    private SimpleAdminUserDto createdBy;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private SimpleAdminUserDto modifiedBy;
}
