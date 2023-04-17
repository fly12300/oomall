//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.dao.openfeign.bo;

import cn.edu.xmu.oomall.order.service.dto.IdNameDto;
import cn.edu.xmu.oomall.order.service.dto.IdNameTypeDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Onsale {
    public static final byte NORMAL = 0;
    public static final byte SHARE = 1;
    public static final byte GROUPON = 2;
    public static final byte ADVANCE_SALE = 3;

    private Long id;
    private IdNameTypeDto shop;
    private IdNameDto product;
    private Long price;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Integer quantity;
    private Integer maxQuantity;
    private Byte type;
    private List<IdNameTypeDto> actList;
}
