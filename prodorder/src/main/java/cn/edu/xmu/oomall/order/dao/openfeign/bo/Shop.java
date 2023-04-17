package cn.edu.xmu.oomall.order.dao.openfeign.bo;

import cn.edu.xmu.oomall.order.service.dto.ConsigneeDto;
import cn.edu.xmu.oomall.order.service.dto.SimpleAdminUserDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class Shop {
    private Long id;
    private String name;
    private Byte type;
    private ConsigneeDto consignee;
    private Byte status;
    private SimpleAdminUserDto creator;
    private Long deposit;
    private Long depositThreshold;
    private Long freeThreshold;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private SimpleAdminUserDto modifier;
}

