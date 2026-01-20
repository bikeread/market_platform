package com.carclub.repository.mapper;

import com.carclub.repository.bean.UploadShippingLogBean;
import com.carclub.repository.model.MtUploadShippingLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 微信小程序上传发货信息 Mapper 接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface MtUploadShippingLogMapper extends BaseMapper<MtUploadShippingLog> {

   List<UploadShippingLogBean> getUploadShippingLogList(@Param("merchantId") Integer merchantId);

}
