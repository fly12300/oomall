package cn.edu.xmu.oomall.freight.service;


import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.bo.Region;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import cn.edu.xmu.oomall.freight.service.dto.UndeliverableDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class UndeliverableServiceTest {
    @Autowired
    private  UndeliverableRegionService service;

    @MockBean
    RedisUtil redisUtil;

    @MockBean
    private RegionDao regionDao;
    @Test
    void testGet(){
        Region region=new Region(483250L,"广东省",(byte)0,(byte)0,"广东","广东","GuangDong",String.valueOf(113.266530),String.valueOf(23.132191),"440000000000","000000",null,null,null,null,null);
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(regionDao.getRegionById(Mockito.any())).thenReturn(new InternalReturnObject<>(region));
        PageDto<UndeliverableDto> shopLogisticId = this.service.getUndeliverableByShopLogisticId(1L, 1L, 1, 10);
        List<UndeliverableDto> list = shopLogisticId.getList();
        assertEquals(list.size(),1);
        assertEquals(list.get(0).getCreator().getUserName(),"admin");
    }

}
