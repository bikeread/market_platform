package com.carclub.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.carclub.common.dto.SmsTemplateDto;
import com.carclub.framework.exception.BusinessCheckException;
import com.carclub.framework.pagination.PaginationRequest;
import com.carclub.framework.pagination.PaginationResponse;
import com.carclub.repository.model.MtSmsTemplate;

import java.util.List;
import java.util.Map;

/**
 * 短信模板业务接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface SmsTemplateService extends IService<MtSmsTemplate> {

    /**
     * 分页查询模板列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtSmsTemplate> querySmsTemplateListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加模板
     *
     * @param reqSmsTemplateDto
     * @throws BusinessCheckException
     */
    MtSmsTemplate saveSmsTemplate(SmsTemplateDto reqSmsTemplateDto) throws BusinessCheckException;

    /**
     * 删除短信模板
     * @param id
     * @param operator
     * @return
     * */
    void deleteTemplate(Integer id, String operator) throws BusinessCheckException;

    /**
     * 根据模板ID获取模板信息
     *
     * @param id ID
     * @throws BusinessCheckException
     */
    MtSmsTemplate querySmsTemplateById(Integer id) throws BusinessCheckException;

    /**
     * 根据条件搜索模板
     * */
    List<MtSmsTemplate> querySmsTemplateByParams(Map<String, Object> params) throws BusinessCheckException;

}
