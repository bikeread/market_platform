package com.carclub.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.carclub.common.dto.CommissionRelationDto;
import com.carclub.framework.pagination.PaginationRequest;
import com.carclub.framework.pagination.PaginationResponse;
import com.carclub.framework.exception.BusinessCheckException;
import com.carclub.repository.model.MtCommissionRelation;
import com.carclub.repository.model.MtUser;

/**
 * 分销提成关系业务接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface CommissionRelationService extends IService<MtCommissionRelation> {

    /**
     * 分页查询分佣关系列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<CommissionRelationDto> queryRelationByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 设置分销提成关系
     *
     * @param  userInfo 会员信息
     * @param  shareId 分享者ID
     * @throws BusinessCheckException
     * @retrurn
     */
    void setCommissionRelation(MtUser userInfo, String shareId) throws BusinessCheckException;

}
