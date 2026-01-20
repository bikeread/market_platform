package com.carclub.repository.mapper;

import com.carclub.repository.model.TAccountDuty;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 *  后台账号角色 Mapper 接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface TAccountDutyMapper extends BaseMapper<TAccountDuty> {

   List<Integer> getDutyIdsByAccountId(Integer accountId);

   void deleteDutiesByAccountId(Integer accountId);

}
