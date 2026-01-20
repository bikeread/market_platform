package com.carclub.repository.mapper;

import com.carclub.repository.model.MtGoodsSku;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品SKU表 Mapper 接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface MtGoodsSkuMapper extends BaseMapper<MtGoodsSku> {

    List<MtGoodsSku> getBySkuNo(@Param("skuNo") String skuNo);

}
