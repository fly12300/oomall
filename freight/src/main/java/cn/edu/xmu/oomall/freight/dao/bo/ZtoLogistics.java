package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.oomall.freight.service.api.ZtoService;
import lombok.Setter;

/**
 * 中通快递
 */
public class ZtoLogistics extends Logistics {
    @Setter
    private ZtoService ztoService;

    @Override
    public String createExpress(Consignee sender, Consignee delivery, String secret) {
        return "zhongtong";
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
