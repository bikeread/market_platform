package com.carclub.common.service;

import com.carclub.common.dto.SettlementDto;
import com.carclub.framework.exception.BusinessCheckException;
import com.carclub.framework.pagination.PaginationRequest;
import com.carclub.framework.pagination.PaginationResponse;
import com.carclub.module.backendApi.request.SettlementRequest;
import com.carclub.repository.model.MtSettlement;

/**
 * 订单结算相关业务接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface SettlementService {

    /**
     * 分页查询结算列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtSettlement> querySettlementListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 提交结算
     *
     * @param  requestParam
     * @throws BusinessCheckException
     * @return
     */
    Boolean submitSettlement(SettlementRequest requestParam) throws BusinessCheckException;

    /**
     * 结算确认
     *
     * @param  settlementId
     * @param  operator
     * @throws BusinessCheckException
     * @return
     */
    Boolean doConfirm(Integer settlementId, String operator) throws BusinessCheckException;

    /**
     * 获取结算详情
     *
     * @param settlementId
     * @param page
     * @param pageSize
     * @return
     * */
    SettlementDto getSettlementInfo(Integer settlementId, Integer page, Integer pageSize) throws BusinessCheckException;
}