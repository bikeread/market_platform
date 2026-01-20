package com.carclub.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carclub.repository.model.MtVehicle;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface MtVehicleMapper extends BaseMapper<MtVehicle> {
    List<MtVehicle> getVehicleByUserId(@Param("userId") Integer userId, @Param("isDefault") Boolean isDefault);
}
