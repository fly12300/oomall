package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.oomall.freight.service.api.SfService;
import lombok.Setter;

/**
 * 顺丰快递
 */
public class SfLogistics extends Logistics {
    @Setter
    private SfService sfService;

    @Override
    public String createExpress(Consignee sender, Consignee delivery, String secret) {
        return "shunfeng";
    }

    @Override
    public Byte getExpressStatus(String billCode, String secret) {
        return null;
    }

    @Override
    public String getExpressRoute(String billCode, String secret) {
        return null;
    }
}
