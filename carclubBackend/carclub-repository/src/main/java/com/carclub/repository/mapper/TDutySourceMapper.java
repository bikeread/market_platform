package com.carclub.repository.mapper;

import com.carclub.repository.model.TDutySource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色菜单 Mapper 接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface TDutySourceMapper extends BaseMapper<TDutySource> {

    void deleteSourcesByDutyId(@Param("dutyId") Integer dutyId);

    List<Long> findSourceIdsByDutyId(@Param("dutyId") Integer dutyId);

}
