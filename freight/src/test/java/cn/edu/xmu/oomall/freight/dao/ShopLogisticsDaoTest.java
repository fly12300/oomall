package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.bo.Logistics;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = FreightApplication.class)
class ShopLogisticsDaoTest {
    @Autowired
    private ShopLogisticsDao shopLogisticsDao;
    @MockBean
    private RedisUtil redisUtil;

    @Test
    void testFindById() {
        ShopLogistics bo = this.shopLogisticsDao.findById(1L);
        assertEquals(bo.getSecret(), "secret1");
        Logistics logistics = bo.getLogistics();
        assertEquals(logistics.getName(), "顺丰快递");
    }

}