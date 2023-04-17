package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.controller.vo.TimeVo;
import cn.edu.xmu.oomall.freight.dao.bo.Undeliverable;
import cn.edu.xmu.oomall.freight.service.ShopLogisticsService;
import cn.edu.xmu.oomall.freight.service.UndeliverableRegionService;
import cn.edu.xmu.oomall.freight.service.WarehouseLogisticsService;
import cn.edu.xmu.oomall.freight.service.dto.UndeliverableDto;
import cn.edu.xmu.oomall.freight.service.dto.WarehouseLogisticsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;


@RestController
@RequestMapping(value = "/shops/{shopId}", produces = "application/json;charset=UTF-8")
public class AdminFreightController {
    private final Logger logger = LoggerFactory.getLogger(AdminFreightController.class);

    private ShopLogisticsService shopLogisticsService;

    private UndeliverableRegionService undeliverableRegionService;

    private WarehouseLogisticsService warehouseLogisticsService;


    public AdminFreightController(ShopLogisticsService shopLogisticsService,UndeliverableRegionService undeliverableRegionService,WarehouseLogisticsService warehouseLogisticsService) {
        this.shopLogisticsService = shopLogisticsService;
        this.undeliverableRegionService=undeliverableRegionService;
        this.warehouseLogisticsService=warehouseLogisticsService;
    }

    @GetMapping("/shoplogistics/{id}/undeliverableregions")
    @Audit(departName = "shops")
    public ReturnObject getUndeliverableregions(@PathVariable("shopId") Long shopId,
                                                @PathVariable("id")Long id,
                                                @RequestParam(required = false,defaultValue = "1") Integer page,
                                                @RequestParam(required = false,defaultValue = "10") Integer pageSize){
        PageDto<UndeliverableDto> pageDto = this.undeliverableRegionService.getUndeliverableByShopLogisticId(shopId, id, page, pageSize);
        return new ReturnObject(pageDto);
    }

    @PostMapping("/shoplogistics/{id}/regions/{rid}/undeliverableregions")
    @Audit(departName = "shops")
    public ReturnObject addUndeliverableregions(@PathVariable("shopId")Long shopId,
                                                @PathVariable("id")Long id,
                                                @PathVariable("rid")Long rid,
                                                @RequestBody TimeVo body,
                                                @LoginUser UserDto userDto){
        this.undeliverableRegionService.insert(shopId, id, rid, body, userDto);
        return new ReturnObject();
    }

    @PutMapping("/shoplogistics/{id}/regions/{rid}/undeliverableregions")
    @Audit(departName = "shops")
    public ReturnObject updateUndeliverableregions(
                                                @PathVariable("shopId")Long shopId,
                                                @PathVariable("id")Long id,
                                                @PathVariable("rid")Long rid,
                                                @RequestBody TimeVo body,
                                                @LoginUser UserDto userDto){
        this.undeliverableRegionService.save(shopId, id, rid, body, userDto);
        return new ReturnObject();
    }

    @DeleteMapping("/shoplogistics/{id}/regions/{rid}/undeliverableregions")
    @Audit(departName = "shops")
    public ReturnObject delUndeliverableregions(@PathVariable("shopId")Long shopId,
                                                @PathVariable("id")Long id,
                                                @PathVariable("rid")Long rid,
                                                @LoginUser UserDto userDto){
        this.undeliverableRegionService.delete(shopId, id, rid, userDto);
        return new ReturnObject();
    }

    @GetMapping("warehouses/{id}/shoplogistics")
    @Audit(departName = "shops")
    public ReturnObject getWareHouseLogistics(@PathVariable("shopId") Long shopId,
                                              @PathVariable("id") Long warehouseId,
                                              @RequestParam(required = false,defaultValue = "1") Integer page,
                                              @RequestParam(required = false,defaultValue = "10") Integer pageSize){
        PageDto<WarehouseLogisticsDto> pageDto =this.warehouseLogisticsService.getWarehouseLogisticsByWarehouseId(shopId,warehouseId,page,pageSize);
        return new ReturnObject(pageDto);
    }

    @DeleteMapping("warehouses/{id}/shoplogistics/{lid}")
    @Audit(departName = "shops")
    public ReturnObject deleteWareHouseLogistics(@PathVariable("shopId") Long shopId,
                                                 @PathVariable("id") Long warehouseId,
                                                 @PathVariable("lid")Long lid,
                                                 @RequestParam(required = false,defaultValue = "1") Integer page,
                                                 @RequestParam(required = false,defaultValue = "10") Integer pageSize,
                                                 @LoginUser UserDto userDto){
        this.warehouseLogisticsService.deleteWarehouseLogisticsByWarehouseId(shopId,warehouseId,lid,page,pageSize,userDto);
        return new ReturnObject();
    }

    @PutMapping("warehouses/{id}/shoplogistics/{lid}")
    @Audit(departName = "shops")
    public ReturnObject updateWarehouseLogistis(@PathVariable("shopId") Long shopId,
                                                @PathVariable("id") Long warehouseId,
                                                @PathVariable("lid")Long lid,
                                                @RequestParam(required = false,defaultValue = "1") Integer page,
                                                @RequestParam(required = false,defaultValue = "10") Integer pageSize,
                                                @RequestBody TimeVo warehouseLogisticsInfo,
                                                @LoginUser UserDto userDto){
        this.warehouseLogisticsService.updateWarehouseLogisticsByWarehouseId(shopId,warehouseId,lid,warehouseLogisticsInfo.getBeginTime(),warehouseLogisticsInfo.getEndTime(),page,pageSize,userDto);
        return new ReturnObject();
    }


    @PostMapping ("warehouses/{id}/shoplogistics/{lid}")
    @Audit(departName = "shops")
    public ReturnObject addWarehouseLogistis(@PathVariable("shopId") Long shopId,
                                             @PathVariable("id") Long warehouseId,
                                             @PathVariable("lid")Long lid,
                                             @RequestBody TimeVo warehouseLogisticsInfo,
                                             @LoginUser UserDto userDto){
        this.warehouseLogisticsService.addWarehouseLogisticsByWarehouseId(shopId,warehouseId,lid,warehouseLogisticsInfo.getBeginTime(),warehouseLogisticsInfo.getEndTime(),userDto);
        return new ReturnObject();
    }



}
