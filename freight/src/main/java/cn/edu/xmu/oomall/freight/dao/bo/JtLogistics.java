package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.oomall.freight.service.api.JtService;
import cn.edu.xmu.oomall.freight.service.api.jtparam.*;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 极兔速递
 */
public class JtLogistics extends Logistics {
    private final Logger logger = LoggerFactory.getLogger(JtLogistics.class);
    @Setter
    private JtService jtService;

    @Override
    public String createExpress(Consignee sender, Consignee delivery, String secret) {
        logger.debug("createExpress: sender = {}, delivery = {}, secret = {}", sender, delivery, secret);
        JtAddOrderRetObject ret = this.jtService.addOrder(new JtAddOrderParam(secret, sender, delivery)).getData();
        return ret.getBillCode();
    }

    @Override
    public Byte getExpressStatus(String billCode, String secret) {
        logger.debug("getExpressStatus: billCode = {}, secret = {}", billCode, secret);
        JtGetOrdersRetObject ret = this.jtService.getOrders(new JtBillCodeParam(secret, billCode)).getData();
        switch (ret.getOrderStatus()) {
            case "104":
                return Express.CANCELED;
            case "103":
            case "102":
            case "101":
                return Express.DELIVERING;
            case "100":
                return Express.UNDELIVERED;
            default:
                return null;
        }
    }

    @Override
    public String getExpressRoute(String billCode, String secret) {
        logger.debug("getExpressRoute: billCode = {}, secret = {}", billCode, secret);
        JtLogisticsTraceRetObject ret = this.jtService.logisticsTrace(new JtBillCodeParam(secret, billCode)).getData();
        return ret.getDetails();
    }
}
