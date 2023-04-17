package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseRegion;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import cn.edu.xmu.oomall.freight.mapper.WarehouseRegionPoMapper;
import cn.edu.xmu.oomall.freight.mapper.po.WarehouseRegionPo;
import cn.edu.xmu.oomall.freight.service.dto.IdNameDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;
import static cn.edu.xmu.javaee.core.util.Common.*;


@Repository
public class WarehouseRegionDao {

    private final Logger logger = LoggerFactory.getLogger(WarehouseRegionDao.class);
    private WarehouseRegionPoMapper maper;
    private RegionDao regionDao;
    private WarehouseDao warehouseDao;

    @Autowired
    public WarehouseRegionDao(WarehouseRegionPoMapper maper, RegionDao regionDao, WarehouseDao warehouseDao) {
        this.maper = maper;
        this.regionDao = regionDao;
        this.warehouseDao = warehouseDao;
    }

    private void setBo(WarehouseRegion bo){
        bo.setWarehouseDao(this.warehouseDao);
        bo.setRegionDao(this.regionDao);
    }
    private WarehouseRegion getBo(WarehouseRegionPo po){
        WarehouseRegion bo = cloneObj(po, WarehouseRegion.class);
        setBo(bo);
        return bo;
    }
    public List<WarehouseRegion> findByShopIdAndRegionId(Long shopId, Long rid, Integer page, Integer pageSize) throws RuntimeException{
        if(null==rid) return null;
        List<WarehouseRegion> res= new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        List<Long> idlist = new ArrayList<>();
        List<IdNameDto> data = regionDao.getParentsRegions(rid).getData();
        if (null!=data){
            idlist = data.stream().map(IdNameDto::getId).collect(Collectors.toList());
        }
        idlist.add(rid);
        List<WarehouseRegionPo> polist = idlist.stream()
                .flatMap(id -> maper.findByRegionId(id, pageable).stream())
                .collect(Collectors.toList());
        logger.debug("findByShopIdAndRegionId: polist = {}", polist);
        if (polist.isEmpty()) {return res;}
        res = polist.stream().map(this::getBo)
                .sorted((o1, o2) -> o2.getWarehouse().getPriority() - o1.getWarehouse().getPriority())
                .collect(Collectors.toList());
        if (PLATFORM != shopId) {
            res = res.stream().filter(bo -> bo.getWarehouse().getShopId().equals(shopId)).collect(Collectors.toList());
        }
        logger.debug("findByShopIdAndRegionId: res = {}", res);
        return res;
    }
    public void saveByBo(WarehouseRegion bo, UserDto user) throws RuntimeException{
        WarehouseRegionPo po = cloneObj(bo, WarehouseRegionPo.class);
        logger.debug("saveWarehouseRegion: po = {}", po);
        po.setId(null);
        putGmtFields(po, "create");
        putUserFields(po, "creator", user);
        logger.debug("saveWarehouseRegionlater: po = {}", po);
        WarehouseRegionPo info = maper.save(po);
        logger.debug("save: po = {}",info);

    }
    public void modifyByBo(WarehouseRegion bo, UserDto user) throws RuntimeException{
        WarehouseRegionPo po = cloneObj(bo, WarehouseRegionPo.class);
        putGmtFields(po, "modified");
        putUserFields(po, "modifier", user);
        logger.debug("modifyWarehouseRegionByBo: po = {}", po);
        Optional<WarehouseRegionPo> opt = maper.findByRegionIdAndWarehouseId(bo.getRegionId(), bo.getWarehouseId());
        if (opt.isEmpty()){
            //TODO 书写格式不明确
            return;//不进行任何操作
//            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,
//                    String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "仓库配送地区", bo.getRegionId()));
        }
        po.setId(opt.get().getId());
        WarehouseRegionPo info = maper.save(po);
        logger.debug("info: po = {}", info);
    }
    public void deleById(Long wid,Long rid) throws RuntimeException{
        if(null==rid||null==wid) return;
        Optional<WarehouseRegionPo> opt = maper.findByRegionIdAndWarehouseId(rid, wid);
        if(opt.isEmpty()){
            //TODO 书写格式不明确
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,
                    String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "仓库配送地区", wid));
        }
        logger.debug("deleById:id = {}",opt.get().getId());
        maper.deleteById(opt.get().getId());
    }

    public List<WarehouseRegion> findByWarehouseId(Long wid,Integer page, Integer pageSize)throws RuntimeException{
        if(null==wid) return null;
        Pageable pageable = PageRequest.of(page-1,pageSize);
        Page<WarehouseRegionPo> polist = maper.findByWarehouseId(wid,pageable);
        if (polist.isEmpty()) return new ArrayList<>();
        List<WarehouseRegion> list = polist.stream().map(this::getBo).collect(Collectors.toList());
        logger.debug("findByWarehouseId: list = {}", list);
        return list;
    }

}
