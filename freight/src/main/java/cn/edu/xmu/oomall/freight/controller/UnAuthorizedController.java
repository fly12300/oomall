package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.oomall.freight.dao.bo.Logistics;
import cn.edu.xmu.oomall.freight.dao.logistics.LogisticsDao;
import cn.edu.xmu.oomall.freight.service.ExpressService;
import cn.edu.xmu.oomall.freight.service.ShopLogisticsService;
import cn.edu.xmu.oomall.freight.service.dto.SimpleLogisticsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController /*Restful的Controller对象*/
@RequestMapping(produces = "application/json;charset=UTF-8")
public class UnAuthorizedController {

    private final Logger logger = LoggerFactory.getLogger(UnAuthorizedController.class);

    private LogisticsDao logisticsDao;

    @Autowired
    public UnAuthorizedController(LogisticsDao logisticsDao){
        this.logisticsDao=logisticsDao;
    }
    @GetMapping("/logistics")
    public ReturnObject getLogistics(@RequestParam("billCode") String billCode){
        SimpleLogisticsDto dto=this.logisticsDao.findByBillCode(billCode);
        return new ReturnObject(dto);
    }
}

