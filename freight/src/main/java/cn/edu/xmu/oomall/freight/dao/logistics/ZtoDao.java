package cn.edu.xmu.oomall.freight.dao.logistics;

import cn.edu.xmu.oomall.freight.dao.bo.Logistics;
import cn.edu.xmu.oomall.freight.dao.bo.ZtoLogistics;
import cn.edu.xmu.oomall.freight.mapper.po.LogisticsPo;
import cn.edu.xmu.oomall.freight.service.api.ZtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

@Repository
public class ZtoDao implements LogisticsInf {
    private final ZtoService ztoService;

    @Autowired
    public ZtoDao(ZtoService ztoService) {
        this.ztoService = ztoService;
    }

    @Override
    public Logistics getLogistics(LogisticsPo po) throws RuntimeException {
        ZtoLogistics bo = cloneObj(po, ZtoLogistics.class);
        bo.setZtoService(this.ztoService);
        return bo;
    }
}
