//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.dao.openfeign.bo;

import cn.edu.xmu.oomall.order.service.dto.IdNameDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product {
    private Long id;
    private String name;
    private IdNameDto template;
    private Integer weight;
}
