package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.controller.vo.WarehouseRegionVo;
import cn.edu.xmu.oomall.freight.dao.WarehouseRegionDao;
import cn.edu.xmu.oomall.freight.dao.bo.Region;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import cn.edu.xmu.oomall.freight.mapper.WarehousePoMapper;
import cn.edu.xmu.oomall.freight.mapper.WarehouseRegionPoMapper;
import cn.edu.xmu.oomall.freight.service.dto.IdNameDto;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;

@SpringBootTest
@AutoConfigureMockMvc
public class WarehouseRegionTest {
    @MockBean
    private RedisUtil redisUtil;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RegionDao regionDao;

    private WarehouseRegionDao warehouseRegionDao;



   // @MockBean
    private WarehouseRegionPoMapper mapper;


    private WarehousePoMapper warehousePoMapper;
    private static String shopToken;

    private static String adminToken;

    private final String GET_REGIONWAREHOUSE = "/shops/{shopId}/regions/{id}/warehouses";

    private final String POST_WAREHOUSEREGION = "/shops/{shopId}/warehouses/{wid}/regions/{id}";

    private final String PUT_WAREHOUSEREGION = "/shops/{shopId}/warehouses/{wid}/regions/{id}";

    private final String DELETE_WAREHOUSEREGION = "/shops/{shopId}/warehouses/{wid}/regions/{id}";

    private final String GET_WAREHOUSEREGION = "/shops/{shopId}/warehouses/{id}/regions";
    @BeforeAll
    public static void start() {
        JwtHelper jwt = new JwtHelper();
        shopToken = jwt.createToken(2L, "1号选手", 2L, 1, 6000);
        adminToken = jwt.createToken(1L, "13088admin", 0L, 1, 3600);
    }
    /**
     * 成功测试
     * @throws Exception
     */
    @Test
    @Transactional
    void retrieveRegionWarehouses1() throws Exception {
        IdNameDto idNameDto = new IdNameDto(0L,"中国");
        List<IdNameDto> list = new ArrayList<>();
        list.add(idNameDto);
        InternalReturnObject<List<IdNameDto>> listInternalReturnObject = new InternalReturnObject<>();
        listInternalReturnObject.setData(list);
        Mockito.when(regionDao.getParentsRegions(Mockito.any()))
                .thenReturn(listInternalReturnObject);
        mockMvc.perform(MockMvcRequestBuilders.get(GET_REGIONWAREHOUSE, 0,1)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].status", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 测试shopid的筛选功能
     * @throws Exception
     */
    @Test
    @Transactional
    void retrieveRegionWarehouses2() throws Exception {
        IdNameDto idNameDto = new IdNameDto(0L,"中国");
        List<IdNameDto> list = new ArrayList<>();
        list.add(idNameDto);
        InternalReturnObject<List<IdNameDto>> listInternalReturnObject = new InternalReturnObject<>();
        listInternalReturnObject.setData(list);
        Mockito.when(regionDao.getParentsRegions(Mockito.any()))
                .thenReturn(listInternalReturnObject);
        mockMvc.perform(MockMvcRequestBuilders.get(GET_REGIONWAREHOUSE, 1,1)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].status", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }
//    /**
//     * 成功测试
//     * 找到regionid 并且shopid匹配
//     * @throws Exception
//     */
//    @Test
//    @Transactional
//    void retrieveRegionWarehouses3() throws Exception {
//
////        Mockito.when(regionWarehouseDao.retrieveRegionWarehousesByshopIdAndId(0L,2L,1,10))
////                .thenReturn(list);
//        mockMvc.perform(MockMvcRequestBuilders.get(GET_REGIONWAREHOUSE, 7,7362)
//                        .header("authorization", adminToken)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .param("page", "1")
//                        .param("pageSize", "10"))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
//                //.andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].status", is(0)))
//                .andDo(MockMvcResultHandlers.print());
//    }
//    /**
//     * 错误测试
//     * 找到regionid 但shopid不匹配
//     * @throws Exception
//     */
//    @Test
//    @Transactional
//    void retrieveRegionWarehouses4() throws Exception {
//
//        mockMvc.perform(MockMvcRequestBuilders.get(GET_REGIONWAREHOUSE, 6,7362)
//                        .header("authorization", adminToken)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .param("page", "1")
//                        .param("pageSize", "10"))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
//                //.andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].status", is(0)))
//                .andDo(MockMvcResultHandlers.print());
//    }

    /**
     * 正常保存
     * @throws Exception
     */
    @Test
    @Transactional
    void  createWarehouseRegion1() throws Exception {
        WarehouseRegionVo vo = new WarehouseRegionVo();
        vo.setBeginTime(LocalDateTime.of(2000,10,1,10,20));
        vo.setEndTime(LocalDateTime.of(2010,11,3,12,22));
        Region region = new Region();
        region.setId(1L);
        Mockito.when(regionDao.getRegionById(2L)).thenReturn(new InternalReturnObject<>(region));
        mockMvc.perform(MockMvcRequestBuilders.post(POST_WAREHOUSEREGION, 0,1,2)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.CREATED.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 仓库id与shopid不匹配
     * @throws Exception
     */
    @Test
    @Transactional
    void  createWarehouseRegion2() throws Exception {
        WarehouseRegionVo vo = new WarehouseRegionVo();
        vo.setBeginTime(LocalDateTime.of(2000,10,1,10,20));
        vo.setEndTime(LocalDateTime.of(2010,11,3,12,22));
        mockMvc.perform(MockMvcRequestBuilders.post(POST_WAREHOUSEREGION, 2,1,2)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());

    }


    /**
     * 仓库id与shopid匹配 但配地区不存在
     * @throws Exception
     */
    @Test
    @Transactional
    void  createWarehouseRegion3() throws Exception {
        WarehouseRegionVo vo = new WarehouseRegionVo();
        vo.setBeginTime(LocalDateTime.of(2000,10,1,10,20));
        vo.setEndTime(LocalDateTime.of(2010,11,3,12,22));
        Mockito.when(regionDao.getRegionById(2L)).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.post(POST_WAREHOUSEREGION, 2,1,2)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());

    }

    /**
     *正常修改
     * @throws Exception
     */
    @Test
    @Transactional
    void  modifyWarehouseRegion1() throws Exception {
        WarehouseRegionVo vo = new WarehouseRegionVo();
        vo.setBeginTime(LocalDateTime.of(2000,10,1,10,20));
        vo.setEndTime(LocalDateTime.of(2010,11,3,12,22));
        Mockito.when(regionDao.getRegionById(2L)).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.put(PUT_WAREHOUSEREGION, 3,3,1)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     *shopid与仓库不对应
     * @throws Exception
     */
    @Test
    @Transactional
    void  modifyWarehouseRegion2() throws Exception {
        WarehouseRegionVo vo = new WarehouseRegionVo();
        vo.setBeginTime(LocalDateTime.of(2000,10,1,10,20));
        vo.setEndTime(LocalDateTime.of(2010,11,3,12,22));
        mockMvc.perform(MockMvcRequestBuilders.put(PUT_WAREHOUSEREGION, 2,3,1)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

//    /**
//     * wid与regionid找不到对应数据
//     * @throws Exception
//     */
//    @Test
//    @Transactional
//    void  modifyWarehouseRegion3() throws Exception {
//        WarehouseRegionVo vo = new WarehouseRegionVo();
//        vo.setBeginTime(LocalDateTime.of(2000,10,1,10,20));
//        vo.setEndTime(LocalDateTime.of(2010,11,3,12,22));
//        mockMvc.perform(MockMvcRequestBuilders.put(PUT_WAREHOUSEREGION, 3,3,5)
//                        .header("authorization", adminToken)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
//                .andExpect(MockMvcResultMatchers.status().is(200))
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
//                .andDo(MockMvcResultHandlers.print());
//    }


    /**
     * 正常删除
     * @throws Exception
     */
    @Test
    @Transactional
    void  delWarehouseRegion1() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_WAREHOUSEREGION, 7,7,7362)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * shopid与wid不对应
     * @throws Exception
     */
    @Test
    @Transactional
    void  delWarehouseRegion2() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_WAREHOUSEREGION, 6,7,7362)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 找不到对应id
     * @throws Exception
     */
    @Test
    @Transactional
    void  delWarehouseRegion3() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_WAREHOUSEREGION, 7,7,7363)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is(404))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 正常访问
     * @throws Exception
     */

    @Test
    @Transactional
    void retrieveWarehouseRegions1() throws Exception {
        Region region = new Region();
        region.setId(1L);
        InternalReturnObject<Region> internalReturnObject = new InternalReturnObject<>();
        internalReturnObject.setData(region);
        Mockito.when(regionDao.getRegionById(Mockito.any())).thenReturn(internalReturnObject);
        mockMvc.perform(MockMvcRequestBuilders.get(GET_WAREHOUSEREGION, 0,2)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }
    /**
     * shopid与wid不对应
     * @throws Exception
     */

    @Test
    @Transactional
    void retrieveWarehouseRegions2() throws Exception {
        Region region = new Region();
        region.setId(1L);
        Mockito.when(regionDao.getRegionById(2L)).thenReturn(new InternalReturnObject<>(region));
        mockMvc.perform(MockMvcRequestBuilders.get(GET_WAREHOUSEREGION, 1,2)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

}
