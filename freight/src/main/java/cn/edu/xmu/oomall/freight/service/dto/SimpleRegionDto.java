package cn.edu.xmu.oomall.freight.service.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class SimpleRegionDto {
    private Integer id;
    private String name;
}
