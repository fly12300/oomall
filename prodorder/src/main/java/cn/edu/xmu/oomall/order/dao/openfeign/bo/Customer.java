package cn.edu.xmu.oomall.order.dao.openfeign.bo;

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
public class Customer {
    private Long id;
    private String userName;
    private String name;
    private Byte invalid;
    private Integer point;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private SimpleAdminUserDto creator;
    private SimpleAdminUserDto modifier;
}
