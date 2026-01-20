package com.carclub.repository.mapper;

import com.carclub.repository.model.MtUserGrade;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 *  Mapper 接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface MtUserGradeMapper extends BaseMapper<MtUserGrade> {
    List<MtUserGrade> getMerchantGradeList(@Param("merchantId") Integer merchantId);
}
