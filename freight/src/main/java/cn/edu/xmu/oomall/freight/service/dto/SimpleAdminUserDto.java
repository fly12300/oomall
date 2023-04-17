package cn.edu.xmu.oomall.freight.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleAdminUserDto {
    @NotNull
    private Long id;
    private String userName;
}