package com.menghor.smart_shop.feature.setting.service;

import com.menghor.smart_shop.enumations.Status;
import com.menghor.smart_shop.feature.setting.dto.request.PlanRequestDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.PlanResponseDto;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;

import java.util.List;

public interface PlanService {
    /**
     * Create a new plan
     * @param planRequestDto Plan request data
     * @return Created plan response
     */
    PlanResponseDto createPlan(PlanRequestDto planRequestDto);

    /**
     * Get a plan by ID
     * @param planId Plan ID
     * @return Plan response
     */
    PlanResponseDto getPlanById(Long planId);

    /**
     * Get all plans with optional filtering and pagination
     * @param name Filter by name (case-insensitive partial match)
     * @param status Filter by status (ACTIVE or INACTIVE)
     * @param pageNo Page number (1-based)
     * @param pageSize Number of items per page
     * @return Paginated list of filtered plans
     */
    CustomPaginationResponseDto<PlanResponseDto> getFilteredPlans(String name, Status status, int pageNo, int pageSize);

    /**
     * Update a plan
     * @param planId Plan ID to update
     * @param planRequestDto Updated plan data
     * @return Updated plan response
     */
    PlanResponseDto updatePlan(Long planId, PlanRequestDto planRequestDto);

    /**
     * Update a plan's status
     * @param planId Plan ID to update
     * @param status New status (ACTIVE or INACTIVE)
     * @return Updated plan response
     */
    PlanResponseDto updatePlanStatus(Long planId, Status status);
}