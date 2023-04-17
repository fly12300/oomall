package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.oomall.freight.dao.bo.Route;
import cn.edu.xmu.oomall.freight.mapper.RoutePoMapper;
import cn.edu.xmu.oomall.freight.mapper.po.RoutePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

@Repository
public class RouteDao {
    private final Logger logger = LoggerFactory.getLogger(RouteDao.class);

    private RoutePoMapper routePoMapper;
    private ExpressDao expressDao;

    @Autowired
    public RouteDao(RoutePoMapper routePoMapper, ExpressDao expressDao) {
        this.routePoMapper = routePoMapper;
        this.expressDao = expressDao;
    }
    private Route getBo(RoutePo po){
        Route obj = cloneObj(po, Route.class);
        return obj;
    }
    public Route findById(String id){
        logger.debug("findById: id = {}", id);
        if (null == id) {
            return null;
        }
        Optional<RoutePo> boOpt = this.routePoMapper.findById(id);
        if (boOpt.isPresent()) {
            return this.getBo(boOpt.get());
        } else {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "路由", id));
        }

    }
}
