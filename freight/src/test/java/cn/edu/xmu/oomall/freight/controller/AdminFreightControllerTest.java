package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.controller.vo.TimeVo;
import cn.edu.xmu.oomall.freight.dao.bo.Region;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import net.minidev.json.JSONUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(classes = FreightApplication.class)
@AutoConfigureMockMvc
@Transactional
public class AdminFreightControllerTest {
    private static String adminToken;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RedisUtil redisUtil;

    @MockBean
    private RegionDao regionDao;

    @BeforeAll
    public static void setup() {
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
    }

    /**
     * 测试获取商铺物流id=1的不可达地区-广东
     * @throws Exception
     */
    @Test
    void testGetUndeliveableRegionByShopLogisticId1() throws Exception {

        Region region=new Region(483250L,"广东省",(byte)0,(byte)0,"广东","广东","GuangDong",String.valueOf(113.266530),String.valueOf(23.132191),"440000000000","000000",null,null,null,null,null);
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(regionDao.getRegionById(Mockito.any())).thenReturn(new InternalReturnObject<>(region));
        mockMvc.perform(MockMvcRequestBuilders.get("/shops/1/shoplogistics/1/undeliverableregions" )
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)

                        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 测试资源不存在异常
     * @throws Exception
     */
    @Test
    void testGetUndeliveableRegionByShopLogisticId2() throws Exception {

        Region region=new Region(483250L,"广东省",(byte)0,(byte)0,"广东","广东","GuangDong",String.valueOf(113.266530),String.valueOf(23.132191),"440000000000","000000",null,null,null,null,null);
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(regionDao.getRegionById(Mockito.any())).thenReturn(new InternalReturnObject<>(region));
        mockMvc.perform(MockMvcRequestBuilders.get("/shops/1/shoplogistics/31/undeliverableregions" )
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page","1")
                        .param("pageSize","10"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 测试商户无权限异常
     * @throws Exception
     */
    @Test
    void testGetUndeliveableRegionByShopLogisticId3() throws Exception {

        Region region=new Region(483250L,"广东省",(byte)0,(byte)0,"广东","广东","GuangDong",String.valueOf(113.266530),String.valueOf(23.132191),"440000000000","000000",null,null,null,null,null);
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(regionDao.getRegionById(Mockito.any())).thenReturn(new InternalReturnObject<>(region));
        mockMvc.perform(MockMvcRequestBuilders.get("/shops/2/shoplogistics/1/undeliverableregions" )
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page","1")
                        .param("pageSize","10"))
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testAddUndeliverableRegion1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        TimeVo vo = new TimeVo();
        vo.setBeginTime(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        vo.setEndTime(LocalDateTime.parse("2023-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        mockMvc.perform(MockMvcRequestBuilders.post("/shops/1/shoplogistics/1/regions/1/undeliverableregions" )
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(JacksonUtil.toJson(vo))
                        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testAddUndeliverableRegion2() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        TimeVo vo = new TimeVo();
        vo.setBeginTime(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        vo.setEndTime(LocalDateTime.parse("2023-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        mockMvc.perform(MockMvcRequestBuilders.post("/shops/2/shoplogistics/1/regions/1/undeliverableregions" )
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(JacksonUtil.toJson(vo))
                )
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testUpdateUndeliverableRegion1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        TimeVo vo = new TimeVo();
        vo.setBeginTime(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        vo.setEndTime(LocalDateTime.parse("2023-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        mockMvc.perform(MockMvcRequestBuilders.put("/shops/1/shoplogistics/1/regions/1/undeliverableregions" )
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(JacksonUtil.toJson(vo))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 测试时间问题
     * @throws Exception
     */
    @Test
    void testUpdateUndeliverableRegion2() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        TimeVo vo = new TimeVo();
        vo.setBeginTime(LocalDateTime.parse("2024-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        vo.setEndTime(LocalDateTime.parse("2023-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        mockMvc.perform(MockMvcRequestBuilders.put("/shops/1/shoplogistics/1/regions/1/undeliverableregions" )
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(JacksonUtil.toJson(vo))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.LATE_BEGINTIME.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testDelUndeliverableRegion1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        TimeVo vo = new TimeVo();
        vo.setBeginTime(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        vo.setEndTime(LocalDateTime.parse("2023-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        mockMvc.perform(MockMvcRequestBuilders.delete("/shops/1/shoplogistics/1/regions/1/undeliverableregions" )
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testDelUndeliverableRegion2() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        TimeVo vo = new TimeVo();
        vo.setBeginTime(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        vo.setEndTime(LocalDateTime.parse("2023-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        mockMvc.perform(MockMvcRequestBuilders.delete("/shops/2/shoplogistics/1/regions/1/undeliverableregions" )
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testGetWarehouseLogistics1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.get("/shops/1/warehouses/1/shoplogistics" )
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 测试商户无权限异常
     * @throws Exception
     */
    @Test
    void testGetWarehouseLogistics2() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.get("/shops/1/warehouses/2/shoplogistics" )
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 测试正常删除
     * @throws Exception
     */
    @Test
    void testDelWarhouseLogistcs1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.delete("/shops/1/warehouses/1/shoplogistics/1" )
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 测试商户无权限异常
     * @throws Exception
     */
    @Test
    void testDelWarhouseLogists2() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.delete("/shops/1/warehouses/2/shoplogistics/1" )
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 测试物流不存在异常
     * @throws Exception
     */
    @Test
    void testDelWarhouseLogistcs3() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.delete("/shops/1/warehouses/1/shoplogistics/999" )
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 测试正常更新仓库物流
     * @throws Exception
     */
    @Test
    void testUpdateWarehouseLogigstics1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        TimeVo vo = new TimeVo();
        vo.setBeginTime(LocalDateTime.parse("2023-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        vo.setEndTime(LocalDateTime.parse("2024-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        mockMvc.perform(MockMvcRequestBuilders.put("/shops/1/warehouses/1/shoplogistics/1" )
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(JacksonUtil.toJson(vo))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 测试商铺无权限
     * @throws Exception
     */
    @Test
    void testUpdateWarehouseLogigstics2() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        TimeVo vo = new TimeVo();
        vo.setBeginTime(LocalDateTime.parse("2023-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        vo.setEndTime(LocalDateTime.parse("2024-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        mockMvc.perform(MockMvcRequestBuilders.put("/shops/2/warehouses/1/shoplogistics/1" )
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(JacksonUtil.toJson(vo))
                )
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 测试时间异常
     * @throws Exception
     */
    @Test
    void testUpdateWarehouseLogigstics3() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        TimeVo vo = new TimeVo();
        vo.setBeginTime(LocalDateTime.parse("2024-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        vo.setEndTime(LocalDateTime.parse("2023-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        mockMvc.perform(MockMvcRequestBuilders.put("/shops/1/warehouses/1/shoplogistics/1" )
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(JacksonUtil.toJson(vo))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.LATE_BEGINTIME.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 测试正常插入数据
     * @throws Exception
     */
    @Test
    void testInsertWarehouseLogistics1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        TimeVo vo = new TimeVo();
        vo.setBeginTime(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        vo.setEndTime(LocalDateTime.parse("2023-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        mockMvc.perform(MockMvcRequestBuilders.post("/shops/1/warehouses/1/shoplogistics/1" )
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(JacksonUtil.toJson(vo))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 测试时间异常
     * @throws Exception
     */
    @Test
    void testInsertWarehouseLogistics2() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        TimeVo vo = new TimeVo();
        vo.setBeginTime(LocalDateTime.parse("2024-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        vo.setEndTime(LocalDateTime.parse("2023-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        mockMvc.perform(MockMvcRequestBuilders.post("/shops/1/warehouses/1/shoplogistics/1" )
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(JacksonUtil.toJson(vo))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.LATE_BEGINTIME.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 测试shopLogistic不存在异常
     * @throws Exception
     */
    @Test
    void testInsertWarehouseLogistics3() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        TimeVo vo = new TimeVo();
        vo.setBeginTime(LocalDateTime.parse("2023-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        vo.setEndTime(LocalDateTime.parse("2024-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        mockMvc.perform(MockMvcRequestBuilders.post("/shops/1/warehouses/1/shoplogistics/9999" )
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(JacksonUtil.toJson(vo))
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }
}
