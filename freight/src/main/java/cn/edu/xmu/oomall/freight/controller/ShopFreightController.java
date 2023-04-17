package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.controller.vo.ShopLogisticsVo;
import cn.edu.xmu.oomall.freight.controller.vo.WarehouseRegionVo;
import cn.edu.xmu.oomall.freight.controller.vo.WarehouseVo;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.service.ShopLogisticsService;
import cn.edu.xmu.oomall.freight.service.WarehouseRegionService;
import cn.edu.xmu.oomall.freight.service.WarehouseService;
import cn.edu.xmu.oomall.freight.service.dto.WarehouseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 以商户为操作人的api的Controller
 */
@RestController
@RequestMapping(value = "/shops/{shopId}", produces = "application/json;charset=UTF-8")
public class ShopFreightController {
    private final Logger logger = LoggerFactory.getLogger(ShopFreightController.class);

    private final WarehouseService warehouseService;

    private final WarehouseRegionService warehouseRegionService;

    private final ShopLogisticsService shopLogisticsService;

    @Autowired
    public ShopFreightController(WarehouseService warehouseService, WarehouseRegionService warehouseRegionService, ShopLogisticsService shopLogisticsService) {
        this.warehouseService = warehouseService;
        this.warehouseRegionService = warehouseRegionService;
        this.shopLogisticsService = shopLogisticsService;
    }

    /**
     * 商户新建仓库
     *
     * @param shopId 商户id
     * @param user   登录用户
     * @param vo     仓库信息
     * @return ReturnObject
     */
    @PostMapping("/warehouses")
    @Audit(departName = "shops")
    public ReturnObject createWarehouse(@PathVariable Long shopId, @LoginUser UserDto user,
                                        @RequestBody @Validated WarehouseVo vo) {
        WarehouseDto dto = this.warehouseService.createWarehouse(shopId, user, vo.getName(), vo.getAddress(),
                vo.getRegionId(), vo.getSenderName(), vo.getSenderMobile());
        return new ReturnObject(ReturnNo.CREATED, dto);
    }

    /**
     * 查询某商户所有仓库
     *
     * @param shopId   商户id
     * @param page     页码
     * @param pageSize 页大小
     * @return ReturnObject
     */
    @GetMapping("/warehouses")
    @Audit(departName = "shops")
    public ReturnObject retrieveWarehouses(@PathVariable Long shopId,
                                           @RequestParam(required = false, defaultValue = "1") Integer page,
                                           @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        PageDto<WarehouseDto> pageDto = this.warehouseService.retrieveWarehouses(shopId, page, pageSize);
        return new ReturnObject(pageDto);
    }

    /**
     * 商户更新仓库
     *
     * @param shopId 商户id
     * @param id     仓库id
     * @param vo     仓库vo
     * @param user   登录用户
     * @return ReturnObject
     */
    @PutMapping("/warehouses/{id}")
    @Audit(departName = "shops")
    public ReturnObject updateWarehouse(@PathVariable Long shopId, @PathVariable Long id,
                                        @RequestBody @Validated WarehouseVo vo, @LoginUser UserDto user) {
        this.warehouseService.updateWarehouse(shopId, id, vo.getName(), vo.getAddress(),
                vo.getRegionId(), vo.getSenderName(), vo.getSenderMobile(), user);
        return new ReturnObject();
    }

    /**
     * 商户暂停仓库发货
     *
     * @param shopId 商户id
     * @param id     仓库id
     * @param user   登录用户
     * @return ReturnObject
     */
    @PutMapping("/warehouses/{id}/suspend")
    @Audit(departName = "shops")
    public ReturnObject suspendWarehouse(@PathVariable Long shopId, @PathVariable Long id, @LoginUser UserDto user) {
        this.warehouseService.setInvalidWarehouse(shopId, id, user, Warehouse.INVALID);
        return new ReturnObject();
    }

    /**
     * 商户恢复仓库发货
     *
     * @param shopId 商户id
     * @param id     仓库id
     * @param user   登录用户
     * @return ReturnObject
     */
    @PutMapping("/warehouses/{id}/resume")
    @Audit(departName = "shops")
    public ReturnObject resumeWarehouse(@PathVariable Long shopId, @PathVariable Long id, @LoginUser UserDto user) {
        this.warehouseService.setInvalidWarehouse(shopId, id, user, Warehouse.VALID);
        return new ReturnObject();
    }

    /**
     * 商户删除仓库
     *
     * @param shopId 商户id
     * @param id     仓库id
     * @return ReturnObject
     */
    @DeleteMapping("/warehouses/{id}")
    @Audit(departName = "shops")
    public ReturnObject deleteWarehouse(@PathVariable Long shopId, @PathVariable Long id) {
        this.warehouseService.deleteWarehouse(shopId, id);
        return new ReturnObject();
    }

    /**
     * 商户或管理员查询某个地区可以配送的所有仓库
     * @param shopId
     * @param id
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/regions/{id}/warehouses")
    @Audit(departName = "shops")
    public ReturnObject getRegionWarehouses(@PathVariable Long shopId, @PathVariable Long id,
                                            @RequestParam(required = false, defaultValue = "1") Integer page,
                                            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return new ReturnObject(this.warehouseRegionService
                .retrieveRegionWarehouses(shopId,id,page,pageSize));
    }



    /**
     * 商户新增仓库配送地区
     * @param shopId
     * @param id
     * @return
     */
    @PostMapping("/warehouses/{wid}/regions/{id}")
    @Audit(departName = "shops")
    public ReturnObject postWarehouseRegion(@PathVariable Long shopId, @PathVariable Long wid,
                                            @PathVariable Long id, @Validated @RequestBody WarehouseRegionVo body,
                                            @LoginUser UserDto user) {
        if (body.getBeginTime().isAfter(body.getEndTime())){
            throw new BusinessException(ReturnNo.LATE_BEGINTIME);
        }
        warehouseRegionService.createWarehouseRegion(shopId,wid,id,body.getBeginTime()
                ,body.getEndTime(),user);
        return new ReturnObject(ReturnNo.CREATED);
    }

    /**
     * 商户修改仓库配送地区
     * @param shopId
     * @param wid
     * @param id
     * @param body
     * @param user
     * @return
     */
    @PutMapping("/warehouses/{wid}/regions/{id}")
    @Audit(departName = "shops")
    public ReturnObject updateWarehouseRegion(@PathVariable Long shopId, @PathVariable Long wid,
                                              @PathVariable Long id, @Validated @RequestBody WarehouseRegionVo body,
                                              @LoginUser UserDto user) {
        if (body.getBeginTime().isAfter(body.getEndTime())){
            throw new BusinessException(ReturnNo.LATE_BEGINTIME);
        }
        warehouseRegionService.modifyWarehouseRegion(shopId,wid,id,body.getBeginTime()
                ,body.getEndTime(),user);
        return new ReturnObject(ReturnNo.OK);
    }

    /**
     * 商户或管理员取消仓库对某个地区的配送
     * @param shopId
     * @param wid
     * @param id
     * @return
     */
    @DeleteMapping ("/warehouses/{wid}/regions/{id}")
    @Audit(departName = "shops")
    public ReturnObject deleteWarehouseRegion(@PathVariable Long shopId, @PathVariable Long wid,
                                              @PathVariable Long id) {
        warehouseRegionService.delWarehouseRegion(shopId,wid,id);
        return new ReturnObject(ReturnNo.OK);
    }


    @GetMapping("/warehouses/{id}/regions")
    @Audit(departName = "shops")
    public ReturnObject getWarehouseRegions(@PathVariable Long shopId, @PathVariable Long id,
                                            @RequestParam(required = false, defaultValue = "1") Integer page,
                                            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return new ReturnObject(this.warehouseRegionService
                .retrieveWarehouseRegions(shopId,id,page,pageSize));
    }

    /**
     * 通过商户id获取商铺物流
     *
     * @param shopId   商户id
     * @param page     页码
     * @param pageSize 页大小
     * @return ReturnObject
     */
    @GetMapping("/shoplogistics")
    @Audit(departName = "shops")
    public ReturnObject retrieveShopLogistics(@PathVariable Long shopId,
                                              @RequestParam(required = false, defaultValue = "1") Integer page,
                                              @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        if (shopId.equals(0L)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商铺物流", shopId));
        }
        return new ReturnObject(this.shopLogisticsService.retrieveShopLogistics(shopId, page, pageSize));
    }

    @PutMapping("/shoplogistics/{id}/suspend")
    @Audit(departName = "shops")
    public ReturnObject suspendShopLogistics(@PathVariable Long shopId,
                                             @PathVariable Long id,
                                             @LoginUser UserDto user) {
        if (shopId.equals(0L)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商铺物流", id, shopId));
        }
        this.shopLogisticsService.updateShopLogisticsStatusById(id, ShopLogistics.VALID, null, user);
        return new ReturnObject(ReturnNo.OK);
    }

    @PutMapping("/shoplogistics/{id}/resume")
    @Audit(departName = "shops")
    public ReturnObject resumeShopLogistics(@PathVariable Long shopId,
                                            @PathVariable Long id,
                                            @LoginUser UserDto user) {
        if (shopId.equals(0L)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商铺物流", id, shopId));
        }
        this.shopLogisticsService.updateShopLogisticsStatusById(id, ShopLogistics.INVALID, null, user);
        return new ReturnObject(ReturnNo.OK);
    }

    @PutMapping("/shoplogistics/{id}")
    @Audit(departName = "shops")
    public ReturnObject updateShopLogisticsById(@PathVariable Long shopId,
                                                @PathVariable Long id,
                                                @Validated @RequestBody ShopLogisticsVo vo,
                                                @LoginUser UserDto user) {
        if (shopId.equals(0L)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商铺物流", shopId));
        }
        this.shopLogisticsService.updateShopLogisticsById(id, vo, user);
        return new ReturnObject(ReturnNo.OK);
    }


}
