package cn.edu.xmu.oomall.freight.dao.logistics;

import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.bo.JtLogistics;
import cn.edu.xmu.oomall.freight.dao.bo.Logistics;
import cn.edu.xmu.oomall.freight.dao.bo.SfLogistics;
import cn.edu.xmu.oomall.freight.dao.bo.ZtoLogistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = FreightApplication.class)
class LogisticsDaoTest {
    @Autowired
    private LogisticsDao logisticsDao;

    @Test
    @Transactional
    public void findById() {
        Logistics sf = this.logisticsDao.findById(1L);
        Logistics zto = this.logisticsDao.findById(2L);
        Logistics jt = this.logisticsDao.findById(3L);
        assertEquals(sf.getClass(), SfLogistics.class);
        assertEquals(zto.getClass(), ZtoLogistics.class);
        assertEquals(jt.getClass(), JtLogistics.class);
        assertEquals(sf.getName(), "顺丰快递");
        assertEquals(zto.getName(), "中通快递");
        assertEquals(jt.getName(), "极兔速递");
    }

    public void te(){

    }
}