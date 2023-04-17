package cn.edu.xmu.oomall.freight.dao.bo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Route  implements Serializable {
    @Getter
    @Setter
    private String id;
    @Getter
    @Setter
    private Long expressId;
    @Getter
    @Setter
    private String content;
    @Getter
    @Setter
    private LocalDateTime gmtCreate;
}
