package cn.edu.xmu.oomall.freight.service.api.jtparam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JtBillCodeParam {
    private String digest;
    private String billCode;
}
