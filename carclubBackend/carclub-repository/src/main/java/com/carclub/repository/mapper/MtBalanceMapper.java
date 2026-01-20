package com.carclub.repository.mapper;

import com.carclub.repository.model.MtBalance;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 余额变化表 Mapper 接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface MtBalanceMapper extends BaseMapper<MtBalance> {

    List<MtBalance> getBalanceListByOrderSn(@Param("orderSn") String orderSn);

}
