package com.carclub.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.carclub.common.dto.GiveDto;
import com.carclub.common.param.GiveParam;
import com.carclub.framework.exception.BusinessCheckException;
import com.carclub.framework.pagination.PaginationRequest;
import com.carclub.framework.pagination.PaginationResponse;
import com.carclub.framework.web.ResponseObject;
import com.carclub.repository.model.MtGive;
import com.carclub.repository.model.MtGiveItem;

import java.util.List;
import java.util.Map;

/**
 * 转赠业务接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface GiveService extends IService<MtGive> {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<GiveDto> queryGiveListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 转赠卡券
     *
     * @param giveParam
     * @throws BusinessCheckException
     * @return
     */
    ResponseObject addGive(GiveParam giveParam) throws BusinessCheckException;

    /**
     * 根据组ID获取信息
     *
     * @param id ID
     * @throws BusinessCheckException
     * @return
     */
    MtGive queryGiveById(Long id) throws BusinessCheckException;

    /**
     * 根据条件搜索转赠详情
     *
     * @param params
     * @throws BusinessCheckException
     * @return
     * */
    List<MtGiveItem> queryItemByParams(Map<String, Object> params);
}
