package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.bo.Region;
import cn.edu.xmu.oomall.freight.dao.bo.Undeliverable;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = FreightApplication.class)
@AutoConfigureMockMvc
@Transactional
public class UndeliverableDaoTest {
    @Autowired
    private UndeliverableDao undeliverableDao;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RedisUtil redisUtil;
    @MockBean
    private RegionDao regionDao;

    @Test
    void testFindById(){

        Region region=new Region(483250L,"广东省",(byte)0,(byte)0,"广东","广东","GuangDong",String.valueOf(113.266530),String.valueOf(23.132191),"440000000000","000000",null,null,null,null,null);
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(regionDao.getRegionById(Mockito.any())).thenReturn(new InternalReturnObject<>(region));
        Undeliverable undeliverable = this.undeliverableDao.findById(1L);
        assertEquals(undeliverable.getRegionId(), 483250L);
        assertEquals(undeliverable.getRegion().getName(), "广东省");

    }

    @Test
    void testRetrieveByShopLogisticId(){

        List<Undeliverable> undeliverables = this.undeliverableDao.retrieveByShopLogisticId(1L, 1, 10);
        assertEquals(undeliverables.size(),1);
        assertEquals(undeliverables.get(0).getRegionId(),483250L);
    }

    @Test
    void testInsert(){
        Undeliverable undeliverable = new Undeliverable();
        undeliverable.setBeginTime(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        undeliverable.setEndTime(LocalDateTime.parse("2023-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        undeliverable.setShopLogisticsId(2L);
        undeliverable.setRegionId(123L);
        this.undeliverableDao.insert(undeliverable,new UserDto(1L,"ky",1L,1));
    }

    @Test
    void testUpdate(){
        Undeliverable undeliverable = new Undeliverable();
        undeliverable.setId(1L);
        undeliverable.setBeginTime(LocalDateTime.parse("2023-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        undeliverable.setEndTime(LocalDateTime.parse("2024-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        undeliverable.setShopLogisticsId(1L);
        undeliverable.setRegionId(483250L);
        this.undeliverableDao.save(undeliverable,new UserDto(1L,"Ky",1L,1));
    }
}
