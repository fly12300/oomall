package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.controller.vo.TimeVo;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.UndeliverableDao;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import cn.edu.xmu.oomall.freight.dao.bo.Undeliverable;
import cn.edu.xmu.oomall.freight.service.dto.SimpleAdminUserDto;
import cn.edu.xmu.oomall.freight.service.dto.SimpleRegionDto;
import cn.edu.xmu.oomall.freight.service.dto.UndeliverableDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.MAX_RETURN;
import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;
import static cn.edu.xmu.javaee.core.model.ReturnNo.LATE_BEGINTIME;
import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

@Service
public class UndeliverableRegionService {
    private static final Logger logger = LoggerFactory.getLogger(UndeliverableRegionService.class);

    private ShopLogisticsDao shopLogisticsDao;

    private UndeliverableDao undeliverableDao;

    private RedisUtil redisUtil;
    @Autowired
    public UndeliverableRegionService(ShopLogisticsDao shopLogisticsDao, RedisUtil redisUtil,UndeliverableDao undeliverableDao) {
        this.shopLogisticsDao = shopLogisticsDao;
        this.redisUtil = redisUtil;
        this.undeliverableDao=undeliverableDao;
    }

    /**
     * 根据shopLogisticId获得相应的不可达信息
     * @param shopId
     * @param shopLogisticId
     * @param page
     * @param pageSize
     * @return
     */
    public PageDto<UndeliverableDto> getUndeliverableByShopLogisticId(Long shopId,Long shopLogisticId,Integer page,Integer pageSize){
        ShopLogistics shopLogistics = this.shopLogisticsDao.findById(shopLogisticId);
        if(PLATFORM!=shopId&&shopLogistics.getShopId()!=shopId){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE,String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(),"商铺物流",shopLogistics.getId(),shopId));
        }
        List<Undeliverable> undeliverable = shopLogistics.getUndeliverable(page, pageSize);
        List<UndeliverableDto> ret = undeliverable.stream().map(bo -> {
            UndeliverableDto dto = cloneObj(bo, UndeliverableDto.class);
            dto.setRegion(SimpleRegionDto.builder().id(Math.toIntExact(bo.getRegionId())).name(bo.getRegion().getName()).build());
            dto.setCreator(new SimpleAdminUserDto(bo.getCreatorId(), bo.getCreatorName()));
            dto.setModifier(new SimpleAdminUserDto(bo.getModifierId(), bo.getModifierName()));
            return dto;
        }).collect(Collectors.toList());
        return new PageDto<>(ret,page,pageSize);
    }

    /**
     * 新增不可达信息
     * @param shopId
     * @param shopLogisticId
     * @param rid
     * @param body
     * @param userDto
     */
    public void insert(Long shopId, Long shopLogisticId, Long rid, TimeVo body, UserDto userDto) {
        ShopLogistics shopLogistics = this.shopLogisticsDao.findById(shopLogisticId);
        if(PLATFORM!=shopId&&shopLogistics.getShopId()!=shopId){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE,String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(),"商铺物流",shopLogistics.getId(),shopId));
        }
        Undeliverable undeliverable = new Undeliverable();
        undeliverable.setRegionId(rid);
        undeliverable.setShopLogisticsId(shopLogisticId);
        undeliverable.setBeginTime(body.getBeginTime());
        undeliverable.setEndTime(body.getEndTime());
        this.undeliverableDao.insert(undeliverable, userDto);
    }

    /**
     * 修改不可达信息
     * @param shopId
     * @param shopLogisticId
     * @param rid
     * @param body
     * @param userDto
     */
    public void save(Long shopId, Long shopLogisticId, Long rid, TimeVo body, UserDto userDto) {
        ShopLogistics shopLogistics = this.shopLogisticsDao.findById(shopLogisticId);
        if(PLATFORM!=shopId&&shopLogistics.getShopId()!=shopId){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE,String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(),"商铺物流",shopLogistics.getId(),shopId));
        }
        if(body.getBeginTime().isAfter(body.getEndTime())){
            throw new BusinessException(ReturnNo.LATE_BEGINTIME,String.format(ReturnNo.LATE_BEGINTIME.getMessage()));
        }
        List<Undeliverable> list = shopLogistics.getUndeliverable(1, MAX_RETURN).stream().filter(bo -> bo.getRegionId().equals(rid)).collect(Collectors.toList());
        if(list.size()>0){
            Undeliverable undeliverable = list.get(0);
            undeliverable.setBeginTime(body.getBeginTime());
            undeliverable.setEndTime(body.getEndTime());
            String key=this.undeliverableDao.save(undeliverable,userDto);
            if(redisUtil.hasKey(key)){
                redisUtil.del(key);
            }
        }
    }

    /**
     * 删除某个不可达信息
     * @param shopId
     * @param shopLogisticId
     * @param rid
     * @param userDto
     */
    public void delete(Long shopId, Long shopLogisticId, Long rid, UserDto userDto) {
        ShopLogistics shopLogistics = this.shopLogisticsDao.findById(shopLogisticId);
        if(PLATFORM!=shopId&&shopLogistics.getShopId()!=shopId){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE,String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(),"商铺物流",shopLogistics.getId(),shopId));
        }
        List<Undeliverable> list = shopLogistics.getUndeliverable(1, MAX_RETURN).stream().filter(bo -> bo.getRegionId().equals(rid)).collect(Collectors.toList());
        if(list.size()>0){
            Undeliverable undeliverable = list.get(0);
            //结束某个不可达
            undeliverable.setEndTime(LocalDateTime.now());
            undeliverable.setBeginTime(undeliverable.getBeginTime().isAfter(LocalDateTime.now())?LocalDateTime.now():undeliverable.getBeginTime());
            String key=this.undeliverableDao.save(undeliverable,userDto);
            if(redisUtil.hasKey(key)){
                redisUtil.del(key);
            }
        }
    }
}
