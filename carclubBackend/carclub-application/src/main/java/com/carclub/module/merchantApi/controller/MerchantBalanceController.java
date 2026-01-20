package com.carclub.module.merchantApi.controller;

import com.carclub.common.dto.UserInfo;
import com.carclub.common.dto.UserOrderDto;
import com.carclub.common.param.RechargeParam;
import com.carclub.common.service.*;
import com.carclub.common.util.TokenUtil;
import com.carclub.framework.exception.BusinessCheckException;
import com.carclub.framework.web.BaseController;
import com.carclub.framework.web.ResponseObject;
import com.carclub.repository.model.MtOrder;
import com.carclub.repository.model.MtStaff;
import com.carclub.repository.model.MtUser;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 余额接口controller
 *
 * Created by FSQ
 * CopyRight carclub
 */
@Api(tags="商户端-余额相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/merchantApi/balance")
public class MerchantBalanceController extends BaseController {

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 支付服务接口
     * */
    private PaymentService paymentService;

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 店铺员工服务接口
     * */
    private StaffService staffService;

    /**
     * 商户服务接口
     * */
    private MerchantService merchantService;

    /**
     * 充值余额
     * */
    @RequestMapping(value = "/doRecharge", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doRecharge(HttpServletRequest request, @RequestBody RechargeParam rechargeParam) throws BusinessCheckException {
        Integer merchantId = merchantService.getMerchantId(request.getHeader("merchantNo"));
        UserInfo userInfo = TokenUtil.getUserInfo();
        if (null == userInfo) {
            return getFailureResult(1001);
        }

        MtStaff staffInfo = null;
        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        if (mtUser != null && mtUser.getMobile() != null) {
            staffInfo = staffService.queryStaffByMobile(mtUser.getMobile());
        }
        if (staffInfo == null) {
            return getFailureResult(201, "您的帐号不是商户，没有操作权限");
        }
        if (!merchantId.equals(staffInfo.getMerchantId())) {
            return getFailureResult(201, "您没有操作权限");
        }
        MtOrder mtOrder = orderService.doRecharge(request, rechargeParam);
        Boolean result = false;
        if (mtOrder != null) {
            UserOrderDto orderInfo = orderService.getOrderByOrderSn(mtOrder.getOrderSn());
            if (orderInfo != null) {
                result = paymentService.paymentCallback(orderInfo);
            }
        }

        return getSuccessResult(result);
    }
}
