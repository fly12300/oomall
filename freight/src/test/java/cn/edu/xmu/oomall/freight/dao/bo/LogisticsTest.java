package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.logistics.LogisticsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = FreightApplication.class)
class LogisticsTest {
    @Autowired
    private LogisticsDao logisticsDao;
    private static final Long SF_ID = 1L;
    private static final Long ZTO_ID = 2L;
    private static final Long JT_ID = 3L;

    @Test
    void createExpressJT() {
        Logistics jt = this.logisticsDao.findById(JT_ID);
        Consignee consignee = new Consignee("张三", "1582341", 5L, "张三街");
        String billCode = jt.createExpress(consignee, consignee, "111");
        assertEquals(billCode, "JT0000498364212");
    }

    @Test
    void getExpressStatusJT() {
        Logistics jt = this.logisticsDao.findById(JT_ID);
        String billCode = "JT0000498364212";
        Byte status = jt.getExpressStatus(billCode, "111");
        assertEquals(status, Express.UNDELIVERED);
    }

    @Test
    void getExpressRouteJT() {
        Logistics jt = this.logisticsDao.findById(JT_ID);
        String billCode = "JT0000498364212";
        String detail = jt.getExpressRoute(billCode, "111");
        assertEquals(detail, "物流信息");
    }
}