package cn.edu.xmu.oomall.freight.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteDto {

    private String content;
    private LocalDateTime gmtCreate;


}
