package com.carclub.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.carclub.common.dto.CommissionRuleDto;
import com.carclub.common.param.CommissionRuleParam;
import com.carclub.framework.pagination.PaginationRequest;
import com.carclub.framework.pagination.PaginationResponse;
import com.carclub.framework.exception.BusinessCheckException;
import com.carclub.repository.model.MtCommissionRule;

/**
 * 分销提成规则业务接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface CommissionRuleService extends IService<MtCommissionRule> {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtCommissionRule> queryDataByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加分佣提成规则
     *
     * @param  commissionRule
     * @throws BusinessCheckException
     */
    MtCommissionRule addCommissionRule(CommissionRuleParam commissionRule) throws BusinessCheckException;

    /**
     * 根据ID获取规则信息
     *
     * @param  id
     * @throws BusinessCheckException
     */
    CommissionRuleDto queryCommissionRuleById(Integer id) throws BusinessCheckException;

    /**
     * 更新分佣提成规则
     *
     * @param  commissionRule
     * @throws BusinessCheckException
     * */
    MtCommissionRule updateCommissionRule(CommissionRuleParam commissionRule) throws BusinessCheckException;

}
