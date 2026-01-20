package com.carclub.repository.mapper;

import com.carclub.repository.model.MtCouponGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 卡券商品表 Mapper 接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface MtCouponGoodsMapper extends BaseMapper<MtCouponGoods> {

    List<MtCouponGoods> getCouponGoods(@Param("couponId") Integer couponId);

}
