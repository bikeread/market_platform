package com.carclub.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.carclub.framework.exception.BusinessCheckException;
import com.carclub.framework.pagination.PaginationRequest;
import com.carclub.framework.pagination.PaginationResponse;
import com.carclub.repository.model.MtUserAction;

/**
 * 会员行为业务接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface UserActionService extends IService<MtUserAction> {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtUserAction> queryUserActionListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 新增会员行为
     *
     * @param  mtUserAction
     * @throws BusinessCheckException
     */
    boolean addUserAction(MtUserAction mtUserAction) throws BusinessCheckException;

    /**
     * 根据ID获取会员行为详情
     *
     * @param  id ID
     * @throws BusinessCheckException
     */
    MtUserAction getUserActionDetail(Integer id) throws BusinessCheckException;

    /**
     * 根据ID删除会员行为
     *
     * @param id ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void deleteUserAction(Integer id, String operator) throws BusinessCheckException;
}
