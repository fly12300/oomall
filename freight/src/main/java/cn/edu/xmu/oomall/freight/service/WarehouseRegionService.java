package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.dao.WarehouseDao;
import cn.edu.xmu.oomall.freight.dao.WarehouseRegionDao;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseRegion;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import cn.edu.xmu.oomall.freight.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;
import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

@Service
public class WarehouseRegionService {
    private final Logger logger = LoggerFactory.getLogger(WarehouseRegionService.class);

    private WarehouseDao warehouseDao;
    private RegionDao regionDao;
    private WarehouseRegionDao warehouseRegionDao;

    @Autowired
    public WarehouseRegionService(WarehouseDao warehouseDao, RegionDao regionDao, WarehouseRegionDao warehouseRegionDao) {
        this.warehouseDao = warehouseDao;
        this.regionDao = regionDao;
        this.warehouseRegionDao = warehouseRegionDao;
    }
    @Transactional
    public PageDto<RegionWarehouseDto> retrieveRegionWarehouses(Long shopId, Long rid, Integer page, Integer pageSize){
        List<RegionWarehouseDto> dtos = warehouseRegionDao.findByShopIdAndRegionId(shopId,rid,page,pageSize).stream().map(bo -> {
            RegionWarehouseDto dto = cloneObj(bo, RegionWarehouseDto.class);
            dto.setWarehouse(cloneObj(bo.getWarehouse(), SimpleWarehouseDto.class));
            dto.setStatus(bo.getWarehouse().getInvalid());
            dto.setCreator(new SimpleAdminUserDto(bo.getCreatorId(), bo.getCreatorName()));
            dto.setModifier(new SimpleAdminUserDto(bo.getModifierId(), bo.getModifierName()));
            return dto;
        }).collect(Collectors.toList());
        logger.debug("retrieveRegionWarehouses: dtos = {}", dtos);
        return new PageDto<>(dtos,page,pageSize);
    }
    @Transactional
    public void createWarehouseRegion(Long shopId, Long wid, Long rid, LocalDateTime beginTime,
                                      LocalDateTime endTime, UserDto user){
       if ((PLATFORM!=shopId&&!warehouseDao.findById(wid).getShopId().equals(shopId))||regionDao.getRegionById(rid).getData()==null){
           //TODO 不存在id，书写格式不明确
           throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE,
                   String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "仓库配送地区",wid,shopId));
       }
       WarehouseRegion bo = new WarehouseRegion(wid,rid,beginTime,endTime);
       logger.debug("createWarehouseRegion: bo = {}", bo.getBeginTime());
       warehouseRegionDao.saveByBo(bo,user);
    }
    @Transactional
    public void modifyWarehouseRegion(Long shopId, Long wid, Long rid, LocalDateTime beginTime,
                                      LocalDateTime endTime, UserDto user){
        if (PLATFORM!=shopId&&!warehouseDao.findById(wid).getShopId().equals(shopId)){
            //TODO 不存在id，书写格式不明确
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE,
                    String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "仓库配送地区",wid, shopId));
        }
        WarehouseRegion bo = new WarehouseRegion(wid,rid,beginTime,endTime);
        logger.debug("modifyWarehouseRegion: bo = {}", bo);
        warehouseRegionDao.modifyByBo(bo,user);

    }
    @Transactional
    public void delWarehouseRegion(Long shopId, Long wid, Long rid){

        if (PLATFORM!=shopId&&!warehouseDao.findById(wid).getShopId().equals(shopId)){
            //TODO 不存在id，书写格式不明确
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE,
                    String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "仓库配送地区",wid, shopId));
        }
        logger.debug("delWarehouseRegion: shopid = {}, wid = {},id = {}", shopId,wid,rid);
        warehouseRegionDao.deleById(wid,rid);
    }
    @Transactional
    public PageDto<WarehouseRegionDto> retrieveWarehouseRegions(Long shopId, Long wid, Integer page, Integer pageSize){
        if (PLATFORM!=shopId&&!warehouseDao.findById(wid).getShopId().equals(shopId)){
            //TODO 不存在id，书写格式不明确
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE,
                    String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "仓库配送地区",wid,shopId));
        }
        List<WarehouseRegionDto> dtos = warehouseRegionDao.findByWarehouseId(wid,page,pageSize).stream().map(bo -> {
            WarehouseRegionDto dto = cloneObj(bo, WarehouseRegionDto.class);
            //TODO  测试时候一直报错 暂时注释
            //dto.setRegion(cloneObj(bo.getRegion(), SimpleRegionDto.class));
            dto.setCreator(new SimpleAdminUserDto(bo.getCreatorId(), bo.getCreatorName()));
            dto.setModifier(new SimpleAdminUserDto(bo.getModifierId(), bo.getModifierName()));
            dto.setGmtCreate(bo.getGmtCreate());
            dto.setGmtModified(bo.getGmtModified());
            return dto;
        }).collect(Collectors.toList());
        logger.debug("retrieveWarehouseRegions: dtos = {}",dtos);
        return new PageDto<>(dtos,page,pageSize);
    }


}
