//package cn.edu.xmu.oomall.freight.mapper;
//
//import cn.edu.xmu.oomall.freight.FreightApplication;
//import cn.edu.xmu.oomall.freight.mapper.po.RoutePo;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest(classes = FreightApplication.class)
//@Transactional
//class RoutePoMapperTest {
//    @Autowired
//    private RoutePoMapper routePoMapper;
//    @Test
//    public void saveTest() {
//        RoutePo po = new RoutePo(null, 199L, "TestRoute");
//        po = this.routePoMapper.save(po);
//        assertNotNull(po.getId());
//    }
//}