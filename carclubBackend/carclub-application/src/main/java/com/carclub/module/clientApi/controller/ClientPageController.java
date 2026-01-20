package com.carclub.module.clientApi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.carclub.common.dto.NavigationDto;
import com.carclub.common.dto.UserInfo;
import com.carclub.common.dto.VehicleDto;
import com.carclub.common.enums.StatusEnum;
import com.carclub.common.service.BannerService;
import com.carclub.common.service.MerchantService;
import com.carclub.common.service.SettingService;
import com.carclub.common.service.VehicleService;
import com.carclub.common.util.TokenUtil;
import com.carclub.framework.exception.BusinessCheckException;
import com.carclub.framework.web.BaseController;
import com.carclub.framework.web.ResponseObject;
import com.carclub.repository.model.MtBanner;
import com.carclub.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 页面接口controller
 *
 * Created by FSQ
 * CopyRight carclub
 */
@Api(tags="会员端-页面相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/page")
public class ClientPageController extends BaseController {

    /**
     * Banner服务接口
     * */
    private BannerService bannerService;

    /**
     * 商户服务接口
     */
    private MerchantService merchantService;

    /**
     * 车辆服务接口
     * */
    private VehicleService vehicleService;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 获取页面数据
     */
    @ApiOperation(value = "获取首页页面数据")
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getPageData(HttpServletRequest request, @RequestParam Map<String, Object> param) throws BusinessCheckException ,JsonProcessingException {
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        Integer storeId = StringUtil.isEmpty(request.getHeader("storeId")) ? 0 : Integer.parseInt(request.getHeader("storeId"));

        UserInfo mtUser = TokenUtil.getUserInfo();

        Map<String, Object> params = new HashMap<>();
        params.put("status", StatusEnum.ENABLED.getKey());
        if (storeId > 0) {
            params.put("storeId", storeId);
        }
        Integer merchantId = merchantService.getMerchantId(merchantNo);
        if (merchantId > 0) {
            params.put("merchantId", merchantId);
        }

        List<MtBanner> bannerData = bannerService.queryBannerListByParams(params);
        VehicleDto vehicle = null;
        if (mtUser != null) {
            List<VehicleDto> vehicles = vehicleService.getVehicleByUserId(mtUser.getId(), true);
            if (vehicles != null && vehicles.size() > 0) {
                vehicle = vehicles.get(0);
            }
        }
        List<NavigationDto> navigation = settingService.getNavigation(merchantId, storeId, StatusEnum.ENABLED.getKey());

        Map<String, Object> outParams = new HashMap();
        outParams.put("banner", bannerData);
        outParams.put("vehicle", vehicle);
        outParams.put("navigation", navigation);
        return getSuccessResult(outParams);
    }
}
