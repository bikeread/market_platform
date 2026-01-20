package com.carclub.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carclub.repository.model.MtCommissionCash;
import org.apache.ibatis.annotations.Param;

/**
 *  提现记录 Mapper 接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface MtCommissionCashMapper extends BaseMapper<MtCommissionCash> {

    Boolean confirmCommissionCash(@Param("merchantId") Integer merchantId, @Param("uuid") String uuid, @Param("operator") String operator);

    Boolean cancelCommissionCash(@Param("merchantId") Integer merchantId, @Param("uuid") String uuid, @Param("operator") String operator);

}
