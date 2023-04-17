package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.dao.ExpressDao;
import cn.edu.xmu.oomall.freight.dao.RouteDao;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.bo.Consignee;
import cn.edu.xmu.oomall.freight.dao.bo.Express;
import cn.edu.xmu.oomall.freight.dao.bo.Logistics;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import cn.edu.xmu.oomall.freight.dao.logistics.LogisticsDao;
import cn.edu.xmu.oomall.freight.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

@Service
public class ExpressService {
    private final Logger logger = LoggerFactory.getLogger(ExpressService.class);

    private ExpressDao expressDao;
    private ShopLogisticsDao shopLogisticsDao;

    private LogisticsDao logisticsDao;

    private RouteDao routeDao;

    @Autowired
    @Lazy
    public ExpressService(ExpressDao expressDao, ShopLogisticsDao shopLogisticsDao, LogisticsDao logisticsDao, RouteDao routeDao) {
        this.expressDao = expressDao;
        this.shopLogisticsDao = shopLogisticsDao;
        this.logisticsDao = logisticsDao;
        this.routeDao = routeDao;
    }


    private Express findValidateExpressByShopId(Long shopId, String billCode) {
        Express bo = expressDao.findByBillCode(billCode);
        if (!shopId.equals(bo.getShopId())) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE,
                    String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "运单", bo.getId(), shopId));
        }
        return bo;
    }
    private Express findValidateExpressById(Long shopId,Long id) {
        Express bo = expressDao.findById(id);
        if (!shopId.equals(bo.getShopId())) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE,
                    String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "运单",bo.getId(), shopId));
        }
        return bo;
    }

    @Transactional
    public SimplePackageDto createExpressByShopId(Long shopId,Consignee sender,Consignee delivery,Long shopLogisticId,UserDto user){
        logger.debug("createExpressByShopId: user = {}", user);
        ShopLogistics shopLogistics = shopLogisticsDao.findById(shopLogisticId);
        if (!shopId.equals(shopLogistics.getShopId())) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE,
                    String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "运单", shopLogistics.getId(), shopId));
        }
        if (ShopLogistics.INVALID.equals(shopLogistics.getInvalid())) {
            throw new BusinessException(ReturnNo.STATENOTALLOW,
                    String.format(ReturnNo.STATENOTALLOW.getMessage(), "商铺物流", shopLogistics.getId(), "无效"));
        }
        Logistics logistics = logisticsDao.findById(shopLogistics.getLogisticsId());
        logger.debug("createExpressByShopId: logistics = {}", logistics);
        String billCode = logistics.createExpress(sender, delivery, logistics.getSecret());
        logger.debug("createExpressByShopId: billCode = {}", billCode);
        Express bo = new Express(billCode, sender.getRegionId(),sender.getAddress(),
                delivery.getRegionId(), delivery.getAddress(),sender.getName(),sender.getMobile(),
                delivery.getName(),delivery.getMobile(), Express.UNDELIVERED,shopId,shopLogisticId);
        logger.debug("createExpressByShopId: bo = {}", bo);
        return expressDao.saveExpressByBo(bo, user);
    }

    @Transactional
    public ExpressDto retrieveExpress(Long shopId,String billCode){
        logger.debug("retrieveExpress: billCode = {}", billCode);
        Express bo = findValidateExpressByShopId(shopId, billCode);
        logger.debug("retrieveExpress: bo = {}", bo);
        ExpressDto dto = cloneObj(bo,ExpressDto.class);
        dto.setLogistics(cloneObj(bo.getLogistics(), SimpleLogisticsDto.class));
        dto.setCreator(new SimpleAdminUserDto(bo.getCreatorId(), bo.getCreatorName()));
        dto.setModifier(new SimpleAdminUserDto(bo.getModifierId(), bo.getModifierName()));
        dto.setShipper(new Consignee(bo.getSenderName(),bo.getSenderMobile(),
                bo.getSenderRegionId(),bo.getSenderAddress()));
        dto.setReceiver(new Consignee(bo.getDeliveryName(),bo.getDeliverMobile(),
                bo.getDeliveryRegionId(),bo.getDeliverAddress()));
        dto.setRoute(cloneObj(routeDao.findById(bo.getId().toString()), RouteDto.class));
        logger.debug("retrieveExpress: dto = {}", dto);
        return dto;
    }

    @Transactional
    public ExpressDto retrieveExpressById(Long id){
        Express bo = expressDao.findById(id);
        logger.debug("retrieveExpressById: bo = {}", bo);
        ExpressDto dto = cloneObj(bo,ExpressDto.class);
        dto.setLogistics(cloneObj(bo.getLogistics(), SimpleLogisticsDto.class));
        dto.setCreator(new SimpleAdminUserDto(bo.getCreatorId(), bo.getCreatorName()));
        dto.setModifier(new SimpleAdminUserDto(bo.getModifierId(), bo.getModifierName()));
        dto.setShipper(new Consignee(bo.getSenderName(),bo.getSenderMobile(),
                bo.getSenderRegionId(),bo.getSenderAddress()));
        dto.setReceiver(new Consignee(bo.getDeliveryName(),bo.getDeliverMobile(),
                bo.getDeliveryRegionId(),bo.getDeliverAddress()));
        dto.setRoute(cloneObj(routeDao.findById(bo.getId().toString()), RouteDto.class));
        logger.debug("retrieveExpressById: dto = {}", dto);
        return dto;
    }

   public void confirmExpressByShopIdAndId(Long shopId,Long id,Byte status,UserDto user){
       Express bo = findValidateExpressById(shopId, id);
       logger.debug("retrieveExpressById: bo = {}", bo);
       status = status==1?Express.SIGNED:Express.REJECTED;
        if (!bo.allowStatus(status)){
            throw new BusinessException(ReturnNo.STATENOTALLOW,
                    String.format(ReturnNo.STATENOTALLOW.getMessage(), "运单", id,bo.getStatusName()));
        }
        bo.setStatus(status);
       logger.debug("retrieveExpressByIdlater: bo = {}", bo);
       expressDao.updateExpress(bo,user);
   }
    public void cancelExpressByShopIdAndId(Long shopId,Long id,UserDto user){
        Express bo = findValidateExpressById(shopId, id);
        Byte status = Express.CANCELED;
        if (!bo.allowStatus(status)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW,
                    String.format(ReturnNo.STATENOTALLOW.getMessage(), "运单", id, bo.getStatusName()));
        }
        bo.setStatus(status);
        expressDao.updateExpress(bo,user);
    }
}
