package com.carclub.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.carclub.common.dto.MemberGroupDto;
import com.carclub.common.dto.UserGroupDto;
import com.carclub.framework.exception.BusinessCheckException;
import com.carclub.framework.pagination.PaginationRequest;
import com.carclub.framework.pagination.PaginationResponse;
import com.carclub.repository.model.MtUserGroup;

/**
 * 会员分组业务接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface MemberGroupService extends IService<MtUserGroup> {

    /**
     * 分页查询分组列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<UserGroupDto> queryMemberGroupListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 新增会员分组
     *
     * @param  memberGroupDto
     * @throws BusinessCheckException
     */
    MtUserGroup addMemberGroup(MemberGroupDto memberGroupDto) throws BusinessCheckException;

    /**
     * 修改卡券分组
     *
     * @param  memberGroupDto
     * @throws BusinessCheckException
     */
    MtUserGroup updateMemberGroup(MemberGroupDto memberGroupDto) throws BusinessCheckException;

    /**
     * 根据组ID获取分组信息
     *
     * @param  id 分组ID
     * @throws BusinessCheckException
     */
    MtUserGroup queryMemberGroupById(Integer id) throws BusinessCheckException;

    /**
     * 根据分组ID删除分组信息
     *
     * @param  id 分组ID
     * @param  operator 操作人
     * @throws BusinessCheckException
     */
    void deleteMemberGroup(Integer id, String operator) throws BusinessCheckException;
}
