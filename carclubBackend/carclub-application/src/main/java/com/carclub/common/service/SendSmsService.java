package com.carclub.common.service;

import com.carclub.framework.exception.BusinessCheckException;
import com.carclub.framework.pagination.PaginationRequest;
import com.carclub.framework.pagination.PaginationResponse;
import com.carclub.repository.model.MtSmsSendedLog;
import java.util.List;
import java.util.Map;

/**
 * 发送短信接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface SendSmsService {

    /**
     * 发送短信方法
     *
     * @param merchantId 商户ID
     * @param tUname     短信模板英文名称
     * @param phones     手机号码集合
     * @return Map<Boolean,List<String>>    TRUE:推送成功的手机号码集合；
     *                                      FALSE:推送失败的手机号码集合
     * @throws Exception
     */
    Map<Boolean, List<String>> sendSms(Integer merchantId, String tUname, List<String> phones, Map<String, String> contentParams) throws BusinessCheckException;

    /**
     * 分页已发短信列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtSmsSendedLog> querySmsListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;
}
