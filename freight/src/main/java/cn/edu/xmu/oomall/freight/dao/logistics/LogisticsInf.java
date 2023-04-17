package cn.edu.xmu.oomall.freight.dao.logistics;

import cn.edu.xmu.oomall.freight.dao.bo.Logistics;
import cn.edu.xmu.oomall.freight.mapper.po.LogisticsPo;

public interface LogisticsInf {
    /**
     * 根据po获取对应的物流
     *
     * @param po 物流po
     * @return Logistics
     */
    Logistics getLogistics(LogisticsPo po) throws RuntimeException;
}
