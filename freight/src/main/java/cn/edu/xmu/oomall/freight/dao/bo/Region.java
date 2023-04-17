package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.oomall.freight.service.dto.IdNameDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class Region {
    private Long id;
    private String name;
    private Byte status;
    private Byte level;
    private String shortName;
    private String mergerName;
    private String pinyin;
    private String lng;
    private String lat;
    private String areaCode;
    private String zipCode;
    private String cityCode;
    private IdNameDto creator;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private IdNameDto modifier;

    public Region(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
