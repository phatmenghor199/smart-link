package com.menghor.smart_shop.feature.setting.service;

import com.menghor.smart_shop.feature.setting.dto.request.PlanRequestDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.PlanResponseDto;

import java.util.List;

public interface PlanService {
    PlanResponseDto createPlan(PlanRequestDto planRequestDto);
    
    PlanResponseDto getPlanById(Long planId);
    
    List<PlanResponseDto> getAllPlans();
    
    List<PlanResponseDto> getActivePlans();
    
    PlanResponseDto updatePlan(Long planId, PlanRequestDto planRequestDto);
    
    PlanResponseDto deactivatePlan(Long planId);
    
    PlanResponseDto activatePlan(Long planId);
}