package com.carclub.repository.mapper;

import com.carclub.repository.model.MtCoupon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 卡券信息表 Mapper 接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface MtCouponMapper extends BaseMapper<MtCoupon> {

    Long queryNumByGroupId(@Param("groupId") Integer groupId);

    List<MtCoupon> queryByGroupId(@Param("groupId") Integer groupId);

}
