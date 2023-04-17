package cn.edu.xmu.oomall.freight.dao.logistics;

import cn.edu.xmu.oomall.freight.dao.bo.Logistics;
import cn.edu.xmu.oomall.freight.dao.bo.SfLogistics;
import cn.edu.xmu.oomall.freight.mapper.po.LogisticsPo;
import cn.edu.xmu.oomall.freight.service.api.SfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

@Repository
public class SfDao implements LogisticsInf {
    private final SfService sfService;

    @Autowired
    public SfDao(SfService sfService) {
        this.sfService = sfService;
    }

    @Override
    public Logistics getLogistics(LogisticsPo po) throws RuntimeException {
        SfLogistics bo = cloneObj(po, SfLogistics.class);
        bo.setSfService(this.sfService);
        return bo;
    }
}
