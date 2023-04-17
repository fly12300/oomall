package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.controller.vo.ExpressVo;
import cn.edu.xmu.oomall.freight.controller.vo.StatusVo;
import cn.edu.xmu.oomall.freight.service.ExpressService;
import cn.edu.xmu.oomall.freight.service.dto.ExpressDto;
import cn.edu.xmu.oomall.freight.service.dto.SimplePackageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping(value = "/internal", produces = "application/json;charset=UTF-8")
public class InternalFreightController {
    private final Logger logger = LoggerFactory.getLogger(InternalFreightController.class);

    private ExpressService expressService;

    @Autowired
    public InternalFreightController(ExpressService expressService) {
        this.expressService = expressService;
    }

    @PostMapping("/shops/{shopId}/packages")
    @Audit(departName = "shops")
    public ReturnObject createExpress(@PathVariable Long shopId, @LoginUser UserDto user,
                                      @RequestBody @Validated ExpressVo body) {
        SimplePackageDto dto = expressService.createExpressByShopId(shopId, body.getSender(),body.getDelivery(),body.getShopLogisticId(),user);
        return new ReturnObject(ReturnNo.CREATED,dto);
    }

    @GetMapping ("/shops/{shopId}/packages")
    @Audit(departName = "shops")
    public ReturnObject getExpress(@PathVariable Long shopId,@RequestParam(required = true) String billCode){
        ExpressDto dto = expressService.retrieveExpress(shopId, billCode);
        return new ReturnObject(dto);
    }

    @GetMapping ("/packages/{id}")
    public ReturnObject getExpressById(@PathVariable Long id){
        ExpressDto dto = expressService.retrieveExpressById(id);
        return new ReturnObject(dto);
    }

    @PutMapping  ("/shops/{shopId}/packages/{id}/confirm")
    @Audit(departName = "shops")
    public ReturnObject confirmExpress(@PathVariable Long shopId, @PathVariable Long id, @RequestBody StatusVo body,
                                       @LoginUser UserDto user){
        expressService.confirmExpressByShopIdAndId(shopId,id,body.getStatus(),user);
        return new ReturnObject(ReturnNo.OK);
    }

    @PutMapping  ("/shops/{shopId}/packages/{id}/cancel")
    @Audit(departName = "shops")
    public ReturnObject cancelExpress(@PathVariable Long shopId, @PathVariable Long id,@LoginUser UserDto user){
        expressService.cancelExpressByShopIdAndId(shopId,id,user);
        return new ReturnObject(ReturnNo.OK);
    }



}
