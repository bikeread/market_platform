package com.carclub.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.carclub.common.dto.PointDto;
import com.carclub.framework.exception.BusinessCheckException;
import com.carclub.framework.pagination.PaginationRequest;
import com.carclub.framework.pagination.PaginationResponse;
import com.carclub.repository.model.MtPoint;

/**
 * 积分业务接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface PointService extends IService<MtPoint> {

    /**
     * 分页查询积分列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<PointDto> queryPointListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加积分
     *
     * @param  reqPointDto
     * @throws BusinessCheckException
     * @return
     */
    void addPoint(MtPoint reqPointDto) throws BusinessCheckException;

    /**
     * 转赠积分
     *
     * @param userId
     * @param mobile
     * @param amount
     * @param remark
     * @throws BusinessCheckException
     * @return
     */
    boolean doGift(Integer userId, String mobile, Integer amount, String remark) throws BusinessCheckException;
}
