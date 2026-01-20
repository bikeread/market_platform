package com.carclub.module.clientApi.controller;

import com.carclub.common.Constants;
import com.carclub.common.dto.PointDto;
import com.carclub.common.dto.UserInfo;
import com.carclub.common.enums.StatusEnum;
import com.carclub.common.param.GivePointParam;
import com.carclub.common.service.PointService;
import com.carclub.common.util.TokenUtil;
import com.carclub.framework.exception.BusinessCheckException;
import com.carclub.framework.pagination.PaginationRequest;
import com.carclub.framework.pagination.PaginationResponse;
import com.carclub.framework.web.BaseController;
import com.carclub.framework.web.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 积分相关controller
 *
 * Created by FSQ
 * CopyRight carclub
 */
@Api(tags="会员端-积分相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/points")
public class ClientPointsController extends BaseController {

    /**
     * 积分服务接口
     */
    private PointService pointService;

    /**
     * 查询我的积分明细
     */
    @ApiOperation(value = "查询我的积分明细")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));

        UserInfo mtUser = TokenUtil.getUserInfo();
        Map<String, Object> param = new HashMap<>();

        param.put("userId", mtUser.getId());
        param.put("status", StatusEnum.ENABLED.getKey());

        PaginationResponse<PointDto> paginationResponse = pointService.queryPointListByPagination(new PaginationRequest(page, pageSize, param));

        return getSuccessResult(paginationResponse);
    }

    /**
     * 转赠积分
     */
    @ApiOperation(value = "转赠积分")
    @RequestMapping(value = "/doGive", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doGive(@RequestBody GivePointParam param) throws BusinessCheckException {
        UserInfo mtUser = TokenUtil.getUserInfo();
        boolean result = pointService.doGift(mtUser.getId(), param.getMobile(), param.getAmount(), param.getRemark());
        if (result) {
            return getSuccessResult(true);
        } else {
            return getFailureResult(3008);
        }
    }
}
