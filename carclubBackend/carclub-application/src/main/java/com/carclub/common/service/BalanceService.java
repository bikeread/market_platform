package com.carclub.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.carclub.common.dto.AccountInfo;
import com.carclub.common.dto.BalanceDto;
import com.carclub.common.param.BalancePage;
import com.carclub.framework.exception.BusinessCheckException;
import com.carclub.framework.pagination.PaginationResponse;
import com.carclub.repository.model.MtBalance;
import java.util.List;

/**
 * 余额业务接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface BalanceService extends IService<MtBalance> {

    /**
     * 分页查询余额列表
     *
     * @param balancePage
     * @return
     */
    PaginationResponse<BalanceDto> queryBalanceListByPagination(BalancePage balancePage) throws BusinessCheckException;

    /**
     * 添加余额记录
     *
     * @param reqDto
     * @param updateBalance
     * @throws BusinessCheckException
     */
    Boolean addBalance(MtBalance reqDto, Boolean updateBalance) throws BusinessCheckException;

    /**
     * 发放余额
     *
     * @param accountInfo
     * @param object
     * @param userIds
     * @param amount
     * @param remark
     * @return
     */
    void distribute(AccountInfo accountInfo, String object, String userIds, String amount, String remark) throws BusinessCheckException;

    /**
     * 获取订单余额记录
     *
     * @param orderSn
     * @return
     * */
    List<MtBalance> getBalanceListByOrderSn(String orderSn) throws BusinessCheckException;
}
