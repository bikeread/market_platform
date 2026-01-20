package com.carclub.module.clientApi.controller;

import com.carclub.common.dto.UserInfo;
import com.carclub.common.enums.UserCouponStatusEnum;
import com.carclub.common.service.CouponService;
import com.carclub.common.service.UserCouponService;
import com.carclub.common.util.TokenUtil;
import com.carclub.framework.exception.BusinessCheckException;
import com.carclub.framework.web.BaseController;
import com.carclub.framework.web.ResponseObject;
import com.carclub.module.clientApi.request.MyCouponRequest;
import com.carclub.repository.model.MtUserCoupon;
import com.carclub.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 我的卡券controller
 *
 * Created by FSQ
 * CopyRight carclub
 */
@Api(tags="会员端-我的卡券相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/myCoupon")
public class ClientMyCouponController extends BaseController {

    /**
     * 卡券服务接口
     */
    private CouponService couponService;

    /**
     * 会员卡券服务接口
     * */
    private UserCouponService userCouponService;

    /**
     * 查询我的卡券
     */
    @ApiOperation(value = "查询我的卡券")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String status = request.getParameter("status") == null ? "" : request.getParameter("status");
        String type = request.getParameter("type") == null ? "" : request.getParameter("type");
        String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");

        UserInfo mtUser = TokenUtil.getUserInfo();
        if (null == mtUser) {
            return getFailureResult(1001);
        }

        if (StringUtil.isNotEmpty(userId)) {
            mtUser.setId(Integer.parseInt(userId));
        }

        Map<String, Object> param = new HashMap<>();
        param.put("userId", mtUser.getId());
        param.put("status", status);
        param.put("type", type);

        ResponseObject result = userCouponService.getUserCouponList(param);

        return getSuccessResult(result.getData());
    }

    /**
     * 查询我的卡券是否已使用
     */
    @ApiOperation(value = "查询我的卡券是否已使用")
    @RequestMapping(value = "/isUsed", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject isUsed(@RequestBody MyCouponRequest requestParam) throws BusinessCheckException {
        Integer userCouponId = requestParam.getId() == null ? 0 : requestParam.getId();
        UserInfo mtUser = TokenUtil.getUserInfo();
        MtUserCoupon userCoupon = couponService.queryUserCouponById(userCouponId);
        if (userCoupon.getStatus().equals(UserCouponStatusEnum.USED.getKey()) && mtUser.getId().equals(userCoupon.getUserId())) {
            return getSuccessResult(true);
        } else {
            return getSuccessResult(false);
        }
    }

    /**
     * 删除我的卡券
     */
    @ApiOperation(value = "删除我的卡券")
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject remove(@RequestBody MyCouponRequest requestParam) throws BusinessCheckException {
        Integer userCouponId = requestParam.getUserCouponId() == null ? 0 : requestParam.getUserCouponId();
        UserInfo mtUser = TokenUtil.getUserInfo();
        Boolean result = couponService.removeCoupon(userCouponId, mtUser.getId());
        if (result) {
            return getSuccessResult(true);
        } else {
            return getSuccessResult(false);
        }
    }
}
