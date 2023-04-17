package cn.edu.xmu.oomall.order.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StatusDto {
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 状态名称
     */
    private String name;
}
