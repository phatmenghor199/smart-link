package com.menghor.smart_shop.feature.setting.controller;

import com.menghor.smart_shop.exceptoins.response.ApiResponse;
import com.menghor.smart_shop.feature.setting.dto.request.PlanRequestDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.PlanResponseDto;
import com.menghor.smart_shop.feature.setting.service.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    
    @GetMapping
    public ApiResponse<List<PlanResponseDto>> getAllPlans() {
        log.info("Received request to get all plans");
        final List<PlanResponseDto> plans = planService.getAllPlans();
        return new ApiResponse<>("Success", "Plans retrieved successfully", plans);
    }
    
    @GetMapping("/active")
    public ApiResponse<List<PlanResponseDto>> getActivePlans() {
        log.info("Received request to get all active plans");
        final List<PlanResponseDto> plans = planService.getActivePlans();
        return new ApiResponse<>("Success", "Active plans retrieved successfully", plans);
    }
    
    @PutMapping("/{planId}")
    public ApiResponse<PlanResponseDto> updatePlan(@PathVariable Long planId, @RequestBody PlanRequestDto planRequestDto) {
        log.info("Received request to update plan with ID: {}", planId);
        final PlanResponseDto planResponseDto = planService.updatePlan(planId, planRequestDto);
        return new ApiResponse<>("Success", "Plan updated successfully", planResponseDto);
    }
    
    @PutMapping("/{planId}/deactivate")
    public ApiResponse<PlanResponseDto> deactivatePlan(@PathVariable Long planId) {
        log.info("Received request to deactivate plan with ID: {}", planId);
        final PlanResponseDto planResponseDto = planService.deactivatePlan(planId);
        return new ApiResponse<>("Success", "Plan deactivated successfully", planResponseDto);
    }
    
    @PutMapping("/{planId}/activate")
    public ApiResponse<PlanResponseDto> activatePlan(@PathVariable Long planId) {
        log.info("Received request to activate plan with ID: {}", planId);
        final PlanResponseDto planResponseDto = planService.activatePlan(planId);
        return new ApiResponse<>("Success", "Plan activated successfully", planResponseDto);
    }
}