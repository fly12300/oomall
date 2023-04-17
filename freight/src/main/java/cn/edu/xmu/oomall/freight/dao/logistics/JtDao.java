package cn.edu.xmu.oomall.freight.dao.logistics;

import cn.edu.xmu.oomall.freight.dao.bo.JtLogistics;
import cn.edu.xmu.oomall.freight.dao.bo.Logistics;
import cn.edu.xmu.oomall.freight.mapper.po.LogisticsPo;
import cn.edu.xmu.oomall.freight.service.api.JtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

@Repository
public class JtDao implements LogisticsInf {
    private final JtService jtService;

    @Autowired
    public JtDao(JtService jtService) {
        this.jtService = jtService;
    }

    @Override
    public Logistics getLogistics(LogisticsPo po) throws RuntimeException {
        JtLogistics bo = cloneObj(po, JtLogistics.class);
        bo.setJtService(this.jtService);
        return bo;
    }
}
