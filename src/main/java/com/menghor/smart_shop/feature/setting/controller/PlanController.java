package com.menghor.smart_shop.feature.setting.controller;

import com.menghor.smart_shop.enumations.Status;
import com.menghor.smart_shop.exceptions.response.ApiResponse;
import com.menghor.smart_shop.feature.setting.dto.request.PlanFilterRequestDto;
import com.menghor.smart_shop.feature.setting.dto.request.PlanRequestDto;
import com.menghor.smart_shop.feature.setting.dto.request.PlanStatusUpdateDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.PlanResponseDto;
import com.menghor.smart_shop.feature.setting.service.PlanService;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;
import com.menghor.smart_shop.utils.pagiantion.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
@Slf4j
public class PlanController {

    private final PlanService planService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PlanResponseDto> createPlan(@RequestBody PlanRequestDto planRequestDto) {
        log.info("Received request to create a new plan");
        final PlanResponseDto planResponseDto = planService.createPlan(planRequestDto);
        return new ApiResponse<>("Success", "Plan created successfully", planResponseDto);
    }

    @GetMapping("/{planId}")
    public ApiResponse<PlanResponseDto> getPlanById(@PathVariable Long planId) {
        log.info("Received request to get plan by ID: {}", planId);
        final PlanResponseDto planResponseDto = planService.getPlanById(planId);
        return new ApiResponse<>("Success", "Plan retrieved successfully", planResponseDto);
    }

    @PostMapping("/all")
    public ApiResponse<CustomPaginationResponseDto<PlanResponseDto>> getAllPlans(
           @RequestBody PlanFilterRequestDto filterRequest) {

        // Validate pagination parameters
        PaginationUtils.validatePagination(
                filterRequest.getPageNo() != null ? filterRequest.getPageNo() : 1,
                filterRequest.getPageSize() != null ? filterRequest.getPageSize() : 10);

        log.info("Received request to get plans with filters: {}", filterRequest);

        final CustomPaginationResponseDto<PlanResponseDto> plansPaginated =
                planService.getFilteredPlans(
                        filterRequest.getName(),
                        filterRequest.getStatus(),
                        filterRequest.getPageNo(),
                        filterRequest.getPageSize());
        return new ApiResponse<>("Success", "Plans retrieved successfully", plansPaginated);
    }

    @PutMapping("/{planId}")
    public ApiResponse<PlanResponseDto> updatePlan(@PathVariable Long planId, @RequestBody PlanRequestDto planRequestDto) {
        log.info("Received request to update plan with ID: {}", planId);
        final PlanResponseDto planResponseDto = planService.updatePlan(planId, planRequestDto);
        return new ApiResponse<>("Success", "Plan updated successfully", planResponseDto);
    }

    @PatchMapping("/{planId}")
    public ApiResponse<PlanResponseDto> updatePlanStatus(
            @PathVariable Long planId,
            @RequestBody PlanStatusUpdateDto statusUpdateDto) {

        log.info("Received request to update status of plan with ID: {} to status: {}",
                planId, statusUpdateDto.getStatus());

        final PlanResponseDto planResponseDto = planService.updatePlanStatus(
                planId, statusUpdateDto.getStatus());

        String message = statusUpdateDto.getStatus() == Status.ACTIVE ?
                "Plan activated successfully" : "Plan deactivated successfully";

        return new ApiResponse<>("Success", message, planResponseDto);
    }
}