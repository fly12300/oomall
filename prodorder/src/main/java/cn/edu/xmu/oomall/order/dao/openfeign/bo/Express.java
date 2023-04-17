package cn.edu.xmu.oomall.order.dao.openfeign.bo;

import cn.edu.xmu.oomall.order.service.dto.ConsigneeDto;
import cn.edu.xmu.oomall.order.service.dto.IdNameDto;
import cn.edu.xmu.oomall.order.service.dto.SimpleAdminUserDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Express {
    private Long id;
    private String billCode;
    private IdNameDto logistics;
    private ConsigneeDto shipper;
    private ConsigneeDto receiver;
    private Byte status;
    private SimpleAdminUserDto creator;
    private SimpleAdminUserDto modifier;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
