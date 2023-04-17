package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.dao.WarehouseDao;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.service.dto.IdNameDto;
import cn.edu.xmu.oomall.freight.service.dto.SimpleAdminUserDto;
import cn.edu.xmu.oomall.freight.service.dto.WarehouseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

@Service
public class WarehouseService {
    private final Logger logger = LoggerFactory.getLogger(WarehouseService.class);

    private final WarehouseDao warehouseDao;
    private final RedisUtil redisUtil;

    @Autowired
    public WarehouseService(WarehouseDao warehouseDao, RedisUtil redisUtil) {
        this.warehouseDao = warehouseDao;
        this.redisUtil = redisUtil;
    }

    /**
     * 用于检查bo和shopId是否匹配
     *
     * @param shopId 商户id
     * @param id     仓库id
     * @return Warehouse
     */
    private Warehouse findValidateWarehouseById(Long shopId, Long id) {
        Warehouse bo = this.warehouseDao.findById(id);
        if (!shopId.equals(bo.getShopId())) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE,
                    String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "仓库", bo.getId(), shopId));
        }
        return bo;
    }

    /**
     * 商户新增仓库
     *
     * @param shopId       商户id
     * @param user         登录用户
     * @param name         仓库名
     * @param address      详细地址
     * @param regionId     地区id
     * @param senderName   联系人
     * @param senderMobile 联系电话
     * @return WarehouseDto
     */
    @Transactional
    public WarehouseDto createWarehouse(Long shopId, UserDto user, String name, String address,
                                        Long regionId, String senderName, String senderMobile) {
        Warehouse bo = new Warehouse(shopId, name, address, regionId, senderName, senderMobile, Warehouse.VALID, 1000);
        logger.debug("createWarehouse: bo = {}", bo);
        this.warehouseDao.insert(bo, user);
        WarehouseDto dto = cloneObj(bo, WarehouseDto.class);
        dto.setRegion(cloneObj(bo.getRegion(), IdNameDto.class));
        dto.setCreatedBy(new SimpleAdminUserDto(user.getId(), user.getName()));
        dto.setStatus((byte) (bo.getInvalid() ^ 1));
        return dto;
    }

    /**
     * 获取某商户所有仓库
     *
     * @param shopId   商户id
     * @param page     页码
     * @param pageSize 页大小
     * @return PageDto
     */
    @Transactional
    public PageDto<WarehouseDto> retrieveWarehouses(Long shopId, Integer page, Integer pageSize) {
        List<WarehouseDto> dtoList = this.warehouseDao.retrieveByShopId(shopId, page, pageSize)
                .stream()
                .sorted(Comparator.comparingInt(Warehouse::getPriority))
                .map(bo -> {
                    WarehouseDto dto = cloneObj(bo, WarehouseDto.class);
                    dto.setRegion(cloneObj(bo.getRegion(), IdNameDto.class));
                    dto.setCreatedBy(new SimpleAdminUserDto(bo.getCreatorId(), bo.getCreatorName()));
                    dto.setModifiedBy(new SimpleAdminUserDto(bo.getModifierId(), bo.getModifierName()));
                    dto.setStatus((byte) (bo.getInvalid() ^ 1));
                    return dto;
                })
                .collect(Collectors.toList());
        return new PageDto<>(dtoList, page, pageSize);
    }

    @Transactional
    public void updateWarehouse(Long shopId, Long id, String name, String address, Long regionId,
                                String senderName, String senderMobile, UserDto user) {
        Warehouse bo = this.findValidateWarehouseById(shopId, id);
        bo.setName(name);
        bo.setAddress(address);
        bo.setRegionId(regionId);
        bo.setSenderName(senderName);
        bo.setSenderMobile(senderMobile);
        logger.debug("updateWarehouse: bo = {}", bo);
        String key = this.warehouseDao.saveById(bo, user);
        this.redisUtil.del(key);
    }

    /**
     * 更新仓库状态：暂停、恢复
     *
     * @param shopId  商户id
     * @param id      仓库id
     * @param user    用户id
     * @param invalid 0有效 1无效
     */
    @Transactional
    public void setInvalidWarehouse(Long shopId, Long id, UserDto user, Byte invalid) {
        Warehouse bo = findValidateWarehouseById(shopId, id);
        bo.setInvalid(invalid);
        logger.debug("setInvalidWarehouse: bo = {}", bo);
        String key = this.warehouseDao.saveById(bo, user);
        this.redisUtil.del(key);
    }

    /**
     * 删除仓库
     *
     * @param shopId 商户id
     * @param id     仓库id
     */
    @Transactional
    public void deleteWarehouse(Long shopId, Long id) {
        Warehouse bo = this.findValidateWarehouseById(shopId, id);
        //TODO: 删除仓库物流、仓库地区
        logger.debug("deleteWarehouse: bo = {}", bo);
        String key = this.warehouseDao.deleteById(bo);
        this.redisUtil.del(key);
    }
}
