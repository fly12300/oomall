package cn.edu.xmu.oomall.freight.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class ShopLogisticsDto {
    private SimpleLogisticsDto logistics;
    private Integer invalid;
    private String secret;
    private Integer priority;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private IdNameDto creator;
    private IdNameDto modifier;
}
