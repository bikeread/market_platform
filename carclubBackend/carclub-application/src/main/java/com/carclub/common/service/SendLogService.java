package com.carclub.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.carclub.common.dto.ReqSendLogDto;
import com.carclub.framework.exception.BusinessCheckException;
import com.carclub.framework.pagination.PaginationRequest;
import com.carclub.framework.pagination.PaginationResponse;
import com.carclub.repository.model.MtSendLog;

/**
 * 发券记录业务接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface SendLogService extends IService<MtSendLog> {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtSendLog> querySendLogListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加记录
     *
     * @param  reqSendLogDto
     * @throws BusinessCheckException
     */
    MtSendLog addSendLog(ReqSendLogDto reqSendLogDto) throws BusinessCheckException;

    /**
     * 根据组ID获取发券记录
     *
     * @param  id ID
     * @throws BusinessCheckException
     */
    MtSendLog querySendLogById(Long id) throws BusinessCheckException;

    /**
     * 删除发券记录
     *
     * @param  id       ID
     * @param  operator 操作人
     * @throws BusinessCheckException
     * @return
     */
    void deleteSendLog(Long id, String operator) throws BusinessCheckException;
}
