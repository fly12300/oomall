package cn.edu.xmu.oomall.freight.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UndeliverableDto {
    private Long id;
    private SimpleRegionDto region;
    private SimpleAdminUserDto creator;
    private SimpleAdminUserDto modifier;

    private LocalDateTime beginTime;
    private LocalDateTime endTime;

    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

}
