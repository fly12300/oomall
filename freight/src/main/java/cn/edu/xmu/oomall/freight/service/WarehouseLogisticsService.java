package cn.edu.xmu.oomall.freight.service;


import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.WarehouseDao;
import cn.edu.xmu.oomall.freight.dao.WarehouseLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseLogistics;
import cn.edu.xmu.oomall.freight.service.dto.ShopLogisticsDto;
import cn.edu.xmu.oomall.freight.service.dto.SimpleLogisticsDto;
import cn.edu.xmu.oomall.freight.service.dto.WarehouseLogisticsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;
import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

@Service
public class WarehouseLogisticsService {
    private static final Logger logger = LoggerFactory.getLogger(WarehouseRegionService.class);
    private WarehouseLogisticsDao warehouseLogisticsDao;

    private RedisUtil redisUtil;

    private WarehouseDao warehouseDao;

    private ShopLogisticsDao shopLogisticsDao;


    @Autowired
    public WarehouseLogisticsService(RedisUtil redisUtil,WarehouseLogisticsDao warehouseLogisticsDao,WarehouseDao warehouseDao,ShopLogisticsDao shopLogisticsDao){
        this.redisUtil=redisUtil;
        this.warehouseLogisticsDao=warehouseLogisticsDao;
        this.warehouseDao=warehouseDao;
        this.shopLogisticsDao=shopLogisticsDao;
    }

    /**
     * 根据warehouseId查询相应的仓库物流并且按照priority从小到大排序
     * @param shopId
     * @param warehouseId
     * @param page
     * @param pageSize
     * @return
     */
    public PageDto<WarehouseLogisticsDto> getWarehouseLogisticsByWarehouseId(Long shopId, Long warehouseId, Integer page, Integer pageSize) {
        Warehouse warehouse = this.warehouseDao.findById(warehouseId);
        if(PLATFORM!=shopId&&warehouse.getShopId()!=shopId){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE,String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(),"仓库物流",warehouse.getId(),shopId));
        }

        List<WarehouseLogistics> warehouseLogisticsList = warehouse.getWarehouseLogistics(page,pageSize);
        List<WarehouseLogisticsDto> ret = warehouseLogisticsList.stream().map(bo -> {
            WarehouseLogisticsDto dto = cloneObj(bo, WarehouseLogisticsDto.class);
            ShopLogisticsDto shopLogisticsDto = cloneObj(bo.getShopLogistics(), ShopLogisticsDto.class);
            shopLogisticsDto.setLogistics(new SimpleLogisticsDto(bo.getShopLogistics().getLogisticsId(),bo.getShopLogistics().getLogistics().getName()));
            dto.setShopLogistics(shopLogisticsDto);
            dto.setStatus(bo.getInvalid()^1);
            return dto;
        }).sorted(Comparator.comparing(dto -> dto.getShopLogistics().getPriority())).collect(Collectors.toList());
        return new PageDto<>(ret,page,pageSize);
    }

    @Transactional
    public void deleteWarehouseLogisticsByWarehouseId(Long shopId, Long warehouseId, Long lid, Integer page, Integer pageSize, UserDto userDto) {
        Warehouse warehouse = this.warehouseDao.findById(warehouseId);
        if(PLATFORM!=shopId&&warehouse.getShopId()!=shopId){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE,String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(),"仓库物流",warehouse.getId(),shopId));
        }
        List<WarehouseLogistics> ret = warehouse.getWarehouseLogistics(page, pageSize).stream().filter(obj -> obj.getShopLogisticsId().equals(lid)).collect(Collectors.toList());
        if(0==ret.size()){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"仓库物流",lid));
        }
        WarehouseLogistics warehouseLogistics = ret.get(0);
        warehouseLogistics.setInvalid((byte)1);
        String key=this.warehouseLogisticsDao.save(warehouseLogistics,userDto);
        redisUtil.del(key);
    }
    @Transactional
    public void updateWarehouseLogisticsByWarehouseId(Long shopId, Long warehouseId, Long lid, LocalDateTime beginTime,LocalDateTime endTime, Integer page, Integer pageSize, UserDto userDto) {
        Warehouse warehouse = this.warehouseDao.findById(warehouseId);
        if(beginTime.isAfter(endTime)){
            throw new BusinessException(ReturnNo.LATE_BEGINTIME,String.format(ReturnNo.LATE_BEGINTIME.getMessage()));
        }
        if(PLATFORM!=shopId&&warehouse.getShopId()!=shopId){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE,String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(),"仓库物流",warehouse.getId(),shopId));
        }
        List<WarehouseLogistics> ret = warehouse.getWarehouseLogistics(page, pageSize).stream().filter(obj -> obj.getShopLogisticsId().equals(lid)).collect(Collectors.toList());
        if(0==ret.size()){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"仓库物流",lid));
        }
        WarehouseLogistics warehouseLogistics = ret.get(0);
        warehouseLogistics.setBeginTime(beginTime);
        warehouseLogistics.setEndTime(endTime);
        String key=this.warehouseLogisticsDao.save(warehouseLogistics,userDto);
        redisUtil.del(key);
    }


    @Transactional
    public void addWarehouseLogisticsByWarehouseId(Long shopId, Long warehouseId, Long lid, LocalDateTime beginTime, LocalDateTime endTime,UserDto userDto) {
        Warehouse warehouse = this.warehouseDao.findById(warehouseId);
        ShopLogistics shopLogistics = this.shopLogisticsDao.findById(lid);

        if(beginTime.isAfter(endTime)){
            throw new BusinessException(ReturnNo.LATE_BEGINTIME,String.format(ReturnNo.LATE_BEGINTIME.getMessage()));
        }
        if(PLATFORM!=shopId&&warehouse.getShopId()!=shopId){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE,String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(),"仓库物流",warehouse.getId(),shopId));
        }
        WarehouseLogistics warehouseLogistics = new WarehouseLogistics();
        warehouseLogistics.setWarehouseId(warehouseId);
        warehouseLogistics.setShopLogisticsId(lid);
        warehouseLogistics.setInvalid((shopLogistics.getInvalid() ==(byte)0?(byte)0:(byte)1));
        warehouseLogistics.setBeginTime(beginTime);
        warehouseLogistics.setEndTime(endTime);
        this.warehouseLogisticsDao.insert(warehouseLogistics,userDto);
    }
}
