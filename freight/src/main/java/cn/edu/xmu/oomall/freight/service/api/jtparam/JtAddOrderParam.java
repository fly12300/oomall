package cn.edu.xmu.oomall.freight.service.api.jtparam;

import cn.edu.xmu.oomall.freight.dao.bo.Consignee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JtAddOrderParam {
    private String digest;
    private Consignee sender;
    private Consignee delivery;
}
