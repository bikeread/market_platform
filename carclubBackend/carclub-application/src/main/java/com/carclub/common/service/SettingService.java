package com.carclub.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.carclub.common.dto.NavigationDto;
import com.carclub.common.dto.ParamDto;
import com.carclub.repository.model.MtSetting;
import com.carclub.framework.exception.BusinessCheckException;
import java.util.List;

/**
 * 配置业务接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface SettingService extends IService<MtSetting> {

    /**
     * 删除配置
     *
     * @param  merchantId 商户ID
     * @param  type 类型
     * @param  name 配置名称
     * @throws BusinessCheckException
     */
    void removeSetting(Integer merchantId, String type, String name) throws BusinessCheckException;

    /**
     * 保存配置
     *
     * @param  mtSetting
     * @throws BusinessCheckException
     */
    MtSetting saveSetting(MtSetting mtSetting) throws BusinessCheckException;

    /**
     * 获取配置列表
     *
     * @param  type 类型
     * @throws BusinessCheckException
     * @return
     */
    List<MtSetting> getSettingList(Integer merchantId, String type) throws BusinessCheckException;

    /**
     * 根据配置名称获取配置信息
     *
     * @param  merchantId 商户ID
     * @param  type 类型
     * @param  name 配置名称
     * @throws BusinessCheckException
     */
    MtSetting querySettingByName(Integer merchantId, String type, String name) throws BusinessCheckException;

    /**
     * 根据配置名称获取配置信息
     *
     * @param  merchantId 商户ID
     * @param storeId 店铺ID
     * @param  type 类型
     * @param  name 配置名称
     * @throws BusinessCheckException
     */
    MtSetting querySettingByName(Integer merchantId, Integer storeId, String type, String name) throws BusinessCheckException;

    /**
     * 获取系统上传文件的根路径
     *
     * @return 本地配置或阿里云的oss域名
     * */
    String getUploadBasePath();

    /**
     * 获取支付方式列表
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param platform 平台
     * @return
     * */
    List<ParamDto> getPayTypeList(Integer merchantId, Integer storeId, String platform) throws BusinessCheckException;

    /**
     * 获取导航栏
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param status 状态
     * @return
     * */
    List<NavigationDto> getNavigation(Integer merchantId, Integer storeId, String status) throws JsonProcessingException;


}
