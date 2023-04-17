//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.shop.ShopApplication;
import cn.edu.xmu.oomall.shop.dao.bo.ProductItem;
import cn.edu.xmu.oomall.shop.dao.bo.template.PieceTemplate;
import cn.edu.xmu.oomall.shop.dao.bo.template.RegionTemplate;
import cn.edu.xmu.oomall.shop.dao.bo.template.TemplateResult;
import cn.edu.xmu.oomall.shop.dao.bo.template.WeightTemplate;
import cn.edu.xmu.oomall.shop.dao.template.RegionTemplateDao;
import cn.edu.xmu.oomall.shop.mapper.po.WeightThresholdPo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShopApplication.class)

public class RegionTemplateDaoTest {

    @Autowired
    RegionTemplateDao regionTemplateDao;

    @MockBean
    RedisUtil redisUtil;

    //@Test
    public void insertData(){
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("admin");

        //莆田
        PieceTemplate bo = new PieceTemplate();
        bo.setTemplateId(2L);
        bo.setRegionId(248059L);
        bo.setTemplateDao("pieceTemplateDao");
        bo.setUnit(2);
        bo.setUpperLimit(10);
        bo.setFirstPrice(500L);
        bo.setFirstItems(2);
        bo.setAdditionalPrice(100L);
        bo.setAdditionalItems(2);
        regionTemplateDao.insert(bo, user);

        //中国
        bo = new PieceTemplate();
        bo.setTemplateId(2L);
        bo.setRegionId(0L);
        bo.setTemplateDao("pieceTemplateDao");
        bo.setUnit(2);
        bo.setUpperLimit(10);
        bo.setFirstPrice(1000L);
        bo.setFirstItems(1);
        bo.setAdditionalPrice(100L);
        bo.setAdditionalItems(2);
        regionTemplateDao.insert(bo, user);

        //北京
        bo = new PieceTemplate();
        bo.setTemplateId(2L);
        bo.setRegionId(1L);
        bo.setTemplateDao("pieceTemplateDao");
        bo.setUnit(2);
        bo.setUpperLimit(10);
        bo.setFirstPrice(1000L);
        bo.setFirstItems(1);
        bo.setAdditionalPrice(4L);
        bo.setAdditionalItems(3);

        //厦门
        WeightTemplate bo1 = new WeightTemplate();
        bo1.setTemplateId(1L);
        bo1.setRegionId(247478L);
        bo1.setTemplateDao("weightTemplateDao");
        bo1.setUnit(100);
        bo1.setUpperLimit(500000);
        bo1.setFirstWeight(500);
        bo1.setFirstWeightPrice(1000L);
        bo1.setThresholds(new ArrayList<>(){
            {
                add(new WeightThresholdPo(10000,100L));
                add(new WeightThresholdPo(50000,50L));
                add(new WeightThresholdPo(100000,10L));
                add(new WeightThresholdPo(300000,5L));
                add(new WeightThresholdPo(500000,0L));
            }
        });
        regionTemplateDao.insert(bo1, user);

        //莆田
        bo1 = new WeightTemplate();
        bo1.setTemplateId(1L);
        bo1.setRegionId(248059L);
        bo1.setTemplateDao("weightTemplateDao");
        bo1.setUnit(100);
        bo1.setUpperLimit(500000);
        bo1.setUnit(100);
        bo1.setUpperLimit(500000);
        bo1.setFirstWeight(500);
        bo1.setFirstWeightPrice(1000L);
        bo1.setThresholds(new ArrayList<>(){
            {
                add(new WeightThresholdPo(10000,100L));
                add(new WeightThresholdPo(50000,50L));
                add(new WeightThresholdPo(100000,10L));
                add(new WeightThresholdPo(300000,5L));
                add(new WeightThresholdPo(500000,0L));
            }
        });
        regionTemplateDao.insert(bo1, user);

        //杭州
        bo1 = new WeightTemplate();
        bo1.setTemplateId(1L);
        bo1.setRegionId(191020L);
        bo1.setTemplateDao("weightTemplateDao");
        bo1.setUnit(100);
        bo1.setUpperLimit(500000);
        bo1.setUnit(100);
        bo1.setUpperLimit(500000);
        bo1.setFirstWeight(500);
        bo1.setFirstWeightPrice(1000L);
        bo1.setThresholds(new ArrayList<>(){
            {
                add(new WeightThresholdPo(10000,100L));
                add(new WeightThresholdPo(50000,50L));
                add(new WeightThresholdPo(100000,50L));
                add(new WeightThresholdPo(300000,50L));
                add(new WeightThresholdPo(500000,50L));
            }
        });
        regionTemplateDao.insert(bo1, user);

        //泉州 251197
        bo1 = new WeightTemplate();
        bo1.setTemplateId(1L);
        bo1.setRegionId(251197L);
        bo1.setTemplateDao("weightTemplateDao");
        bo1.setUnit(100);
        bo1.setUpperLimit(500000);
        bo1.setUnit(100);
        bo1.setUpperLimit(500000);
        bo1.setFirstWeight(500);
        bo1.setFirstWeightPrice(1000L);
        bo1.setThresholds(new ArrayList<>(){
            {
                add(new WeightThresholdPo(10000,100L));
                add(new WeightThresholdPo(50000,50L));
                add(new WeightThresholdPo(100000,50L));
                add(new WeightThresholdPo(300000,50L));
                add(new WeightThresholdPo(500000,50L));
            }
        });
        regionTemplateDao.insert(bo1, user);

    }

    @Test
    @Transactional
    public void insert1(){
        PieceTemplate bo = new PieceTemplate();
        bo.setRegionId(152L);
        bo.setTemplateDao("pieceTemplateDao");
        bo.setUnit(1);
        bo.setUpperLimit(10);
        bo.setFirstPrice(1L);
        bo.setFirstItems(2);
        bo.setAdditionalPrice(2L);
        bo.setAdditionalItems(10);

        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("admin");

        regionTemplateDao.insert(bo, user);
        assertNotNull(bo.getId());

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        RegionTemplate regionTemplate = regionTemplateDao.findById(bo.getId());
        assertTrue(regionTemplate instanceof PieceTemplate);
        PieceTemplate bo1 = (PieceTemplate) regionTemplate;
        assertEquals(bo.getUnit(), bo1.getUnit());
        assertEquals(bo.getAdditionalItems(), bo1.getAdditionalItems());
    }

    @Test
    @Transactional
    public void findById1(){

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);


        RegionTemplate bo = regionTemplateDao.findById(105L);
        assertEquals(105L, bo.getId());
        assertTrue(bo instanceof PieceTemplate);
        PieceTemplate bo1 = (PieceTemplate) bo;
        assertEquals(10, bo1.getUpperLimit());

        List<ProductItem> items = new ArrayList<>(){
            {
                add(new ProductItem(1L,1L,100L,1,100000L,1));
                add(new ProductItem(2L,2L,100L,2,100000L,2));
                add(new ProductItem(3L,3L,100L,3,100000L,3));
                add(new ProductItem(4L,4L,100L,4,100000L,4));
                add(new ProductItem(5L,5L,100L,5,100000L,3));
                add(new ProductItem(6L,6L,100L,6,100000L,2));
                add(new ProductItem(7L,7L,100L,7,100000L,1));
            }
        };

        Collection<TemplateResult> results = bo1.calculate(items);
        assertNotNull(results);
        results.stream().forEach(result ->{
                    assertTrue(result.getPack().stream().map(item -> item.getQuantity() ).reduce((x,y) -> x+y).get() <= 10);
                }
        );
        assertEquals(16, results.stream().map(result -> result.getPack().stream().map(item -> item.getQuantity()).reduce((x,y)->x + y).get()).reduce((x,y) -> x + y).get());
        assertEquals(7, results.stream().map(result -> result.getPack().size()).reduce((x,y) -> x + y).get());
        assertEquals(2, results.size());
        assertEquals(1600, results.stream().map(result -> result.getFee()).reduce((x,y) -> x + y).get());
    }

    @Test
    @Transactional
    public void findById2(){

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);


        RegionTemplate bo = regionTemplateDao.findById(107L);
        assertEquals(107L, bo.getId());
        assertTrue(bo instanceof WeightTemplate);
        WeightTemplate bo1 = (WeightTemplate) bo;
        assertEquals(500000, bo1.getUpperLimit());

        List<ProductItem> items = new ArrayList<>(){
            {
                add(new ProductItem(1L,1L,100L,100,100000L,1));
                add(new ProductItem(2L,2L,100L,200,100000L,2));
                add(new ProductItem(3L,3L,100L,300,100000L,3));
                add(new ProductItem(4L,4L,100L,400,100000L,4));
                add(new ProductItem(5L,5L,100L,500,100000L,3));
                add(new ProductItem(6L,6L,100L,600,100000L,2));
                add(new ProductItem(7L,7L,100L,700,100000L,1));
            }
        };

        Collection<TemplateResult> results = bo1.calculate(items);
        assertNotNull(results);
        results.stream().forEach(result ->{
                    assertTrue(result.getPack().stream().map(item -> item.getQuantity() ).reduce((x,y) -> x+y).get() <= 500000);
                }
        );
        assertEquals(16, results.stream().map(result -> result.getPack().stream().map(item -> item.getQuantity()).reduce((x,y)->x + y).get()).reduce((x,y) -> x + y).get());
        assertEquals(7, results.stream().map(result -> result.getPack().size()).reduce((x,y) -> x + y).get());
        assertEquals(1, results.size());
        assertEquals(6900, results.stream().map(result -> result.getFee()).reduce((x,y) -> x + y).get());
    }
}
