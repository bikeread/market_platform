package com.carclub.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.carclub.common.dto.VehicleOrderDto;
import com.carclub.framework.exception.BusinessCheckException;
import com.carclub.framework.pagination.PaginationRequest;
import com.carclub.framework.pagination.PaginationResponse;
import com.carclub.repository.model.MtVehicleOrder;
import java.util.List;
import java.util.Map;

public interface VehicleOrderService extends IService<MtVehicleOrder> {

    MtVehicleOrder updateVehicleOrder(MtVehicleOrder mtVehicleOrder);

    MtVehicleOrder submitVehicleOrder(MtVehicleOrder mtVehicleOrder) throws BusinessCheckException;

    PaginationResponse<VehicleOrderDto> getVehicleOrderListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    MtVehicleOrder getVehicleOrderById(Integer id);

    List<MtVehicleOrder> queryVehicleOrderList(Map<String, Object> paramMap);

    void deleteVehicleOrder(Integer id, String operator) throws BusinessCheckException;
}
