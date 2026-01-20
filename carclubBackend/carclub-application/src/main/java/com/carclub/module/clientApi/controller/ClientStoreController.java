package com.carclub.module.clientApi.controller;

import com.carclub.common.dto.StoreInfo;
import com.carclub.common.service.StoreService;
import com.carclub.framework.exception.BusinessCheckException;
import com.carclub.framework.web.BaseController;
import com.carclub.framework.web.ResponseObject;
import com.carclub.repository.model.MtStore;
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
 * 店铺接口controller
 *
 * Created by FSQ
 * CopyRight carclub
 */
@Api(tags="会员端-店铺相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/store")
public class ClientStoreController extends BaseController {

    /**
     * 店铺服务接口
     * */
    private StoreService storeService;

    /**
     * 获取店铺列表（根据距离排序）
     */
    @ApiOperation(value = "获取店铺列表（根据距离排序）")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String keyword = param.get("keyword") == null ? "" : param.get("keyword").toString();
        String latitude = request.getHeader("latitude") == null ? "" : request.getHeader("latitude");
        String longitude = request.getHeader("longitude") == null ? "" : request.getHeader("longitude");
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");

        List<StoreInfo> storeList = storeService.queryByDistance(merchantNo, keyword, latitude, longitude);

        Map<String, Object> outParams = new HashMap<>();
        outParams.put("data", storeList);

        return getSuccessResult(outParams);
    }

    /**
     * 获取店铺详情
     */
    @ApiOperation(value = "获取店铺详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request) throws BusinessCheckException {
        Integer storeId = StringUtil.isEmpty(request.getHeader("storeId")) ? 0 : Integer.parseInt(request.getHeader("storeId"));

        MtStore storeInfo = storeService.queryStoreById(storeId);

        Map<String, Object> outParams = new HashMap<>();
        outParams.put("storeInfo", storeInfo);

        return getSuccessResult(outParams);
    }
}
