package com.carclub.repository.mapper;

import com.carclub.repository.model.MtMerchant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商户表 Mapper 接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface MtMerchantMapper extends BaseMapper<MtMerchant> {

    MtMerchant queryMerchantByName(@Param("name") String name);

    MtMerchant queryMerchantByNo(@Param("merchantNo") String merchantNo);

}
