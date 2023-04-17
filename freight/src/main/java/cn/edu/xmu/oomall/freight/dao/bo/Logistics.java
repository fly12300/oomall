package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Logistics extends OOMallObject implements Serializable {
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String secret;
    @Getter
    @Setter
    private String appId;
    @Getter
    @Setter
    private String snPattern;
    @Getter
    @Setter
    private String logisticsClass;

    /**
     * 创建运单
     *
     * @param sender   送货人
     * @param delivery 收货人
     * @param secret   密钥
     * @return 运单号
     */
    public abstract String createExpress(Consignee sender, Consignee delivery, String secret);

    /**
     * 获取运单状态
     *
     * @param billCode 运单号
     * @param secret   密钥
     * @return 运单状态
     */
    public abstract Byte getExpressStatus(String billCode, String secret);

    /**
     * 获取运单路线
     *
     * @param billCode 运单号
     * @param secret   密钥
     * @return 路线详情
     */
    public abstract String getExpressRoute(String billCode, String secret);
}
