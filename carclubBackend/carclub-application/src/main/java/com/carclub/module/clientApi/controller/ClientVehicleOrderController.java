package com.carclub.module.clientApi.controller;

import com.carclub.common.dto.UserInfo;
import com.carclub.common.dto.VehicleOrderDto;
import com.carclub.common.param.OrderListParam;
import com.carclub.common.service.VehicleOrderService;
import com.carclub.common.util.TokenUtil;
import com.carclub.framework.exception.BusinessCheckException;
import com.carclub.framework.pagination.PaginationRequest;
import com.carclub.framework.pagination.PaginationResponse;
import com.carclub.framework.web.BaseController;
import com.carclub.framework.web.ResponseObject;
import com.carclub.repository.model.MtVehicleOrder;
import com.carclub.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务单类controller
 *
 * Created by FSQ
 * CopyRight carclub
 */
@Api(tags="会员端-服务单相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/vehicleOrder")
public class ClientVehicleOrderController extends BaseController {

    /**
     * 服务单服务接口
     * */
    private VehicleOrderService vehicleOrderService;

    /**
     * 获取我的订单列表
     */
    @ApiOperation(value = "获取我的服务单列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, @RequestBody OrderListParam orderListParam) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userInfo.getId());
        params.put("status", orderListParam.getStatus());

        PaginationResponse<VehicleOrderDto> paginationResponse = vehicleOrderService.getVehicleOrderListByPagination(new PaginationRequest(orderListParam.getPage(), orderListParam.getPageSize(), params));
        return getSuccessResult(paginationResponse);
    }

    /**
     * 获取服务单详情
     */
    @ApiOperation(value = "获取服务单详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request) throws BusinessCheckException {
        String orderId = request.getParameter("orderId");
        if (StringUtil.isEmpty(orderId)) {
            return getFailureResult(201, "服务单ID不能为空");
        }
        MtVehicleOrder orderInfo = vehicleOrderService.getVehicleOrderById(Integer.parseInt(orderId));
        return getSuccessResult(orderInfo);
    }
}
