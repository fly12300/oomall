package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.bo.Undeliverable;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseLogistics;
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
public class WarehouseLogisticDaoTest
{
    @Autowired
    private WarehouseLogisticsDao warehouseLogisticsDao;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RedisUtil redisUtil;


    @Test
    void testFindById(){
        WarehouseLogistics warehouseLogistics = this.warehouseLogisticsDao.findById(1L);
        assertEquals(warehouseLogistics.getWarehouseId(),1L);
    }

    @Test
    void testgetByWarehouseId(){
        List<WarehouseLogistics> list = this.warehouseLogisticsDao.getByWarehouseId(1L, 1, 10);
        assertEquals(list.size(),4L);
    }

    @Test
    void testSave(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        UserDto userDto = new UserDto(1L, "admin11", 1L, 1);
        WarehouseLogistics obj = new WarehouseLogistics();
        obj.setWarehouseId(1L);
        obj.setId(1L);
        obj.setInvalid(Byte.valueOf("0"));
        obj.setBeginTime(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        obj.setEndTime(LocalDateTime.parse("2024-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        obj.setModifierId(userDto.getId());
        obj.setModifierName(userDto.getName());
        String s = this.warehouseLogisticsDao.save(obj, userDto);
        assertEquals(s,String.format("WL%d",obj.getId()));
    }


}
