package com.menghor.smart_shop.feature.setting.service.impl;

import com.menghor.smart_shop.enumations.Status;
import com.menghor.smart_shop.exceptoins.error.BadRequestException;
import com.menghor.smart_shop.exceptoins.error.NotFoundException;
import com.menghor.smart_shop.feature.setting.dto.request.PlanRequestDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.PlanResponseDto;
import com.menghor.smart_shop.feature.setting.mapper.PlanMapper;
import com.menghor.smart_shop.feature.setting.model.PlanEntity;
import com.menghor.smart_shop.feature.setting.repository.PlanRepository;
import com.menghor.smart_shop.feature.setting.service.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanServiceImpl implements PlanService {
    
    private final PlanRepository planRepository;
    private final PlanMapper planMapper;
    
    @Override
    public PlanResponseDto createPlan(PlanRequestDto planRequestDto) {
        log.info("Creating new plan: {}", planRequestDto.getName());
        
        PlanEntity planEntity = planMapper.toEntity(planRequestDto);
        planEntity.setStatus(Status.ACTIVE);
        
        PlanEntity savedPlan = planRepository.save(planEntity);
        log.info("Plan created successfully with ID: {}", savedPlan.getId());
        
        return planMapper.toDto(savedPlan);
    }
    
    @Override
    public PlanResponseDto getPlanById(Long planId) {
        log.info("Getting plan by ID: {}", planId);
        
        PlanEntity planEntity = planRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan not found with ID: " + planId));
        
        return planMapper.toDto(planEntity);
    }
    
    @Override
    public List<PlanResponseDto> getAllPlans() {
        log.info("Getting all plans");
        
        List<PlanEntity> plans = planRepository.findAll();
        return plans.stream()
                .map(planMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PlanResponseDto> getActivePlans() {
        log.info("Getting all active plans");
        
        List<PlanEntity> plans = planRepository.findByStatusOrderByPriceAsc(Status.ACTIVE);
        return plans.stream()
                .map(planMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public PlanResponseDto updatePlan(Long planId, PlanRequestDto planRequestDto) {
        log.info("Updating plan with ID: {}", planId);
        
        PlanEntity planEntity = planRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan not found with ID: " + planId));
        
        planMapper.updatePlanFromDto(planRequestDto, planEntity);
        PlanEntity updatedPlan = planRepository.save(planEntity);
        
        log.info("Plan updated successfully with ID: {}", updatedPlan.getId());
        
        return planMapper.toDto(updatedPlan);
    }
    
    @Override
    public PlanResponseDto deactivatePlan(Long planId) {
        log.info("Deactivating plan with ID: {}", planId);
        
        PlanEntity planEntity = planRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan not found with ID: " + planId));
        
        if (planEntity.getStatus() == Status.INACTIVE) {
            throw new BadRequestException("Plan is already inactive.");
        }
        
        planEntity.setStatus(Status.INACTIVE);
        PlanEntity updatedPlan = planRepository.save(planEntity);
        
        log.info("Plan deactivated successfully with ID: {}", updatedPlan.getId());
        
        return planMapper.toDto(updatedPlan);
    }
    
    @Override
    public PlanResponseDto activatePlan(Long planId) {
        log.info("Activating plan with ID: {}", planId);
        
        PlanEntity planEntity = planRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan not found with ID: " + planId));
        
        if (planEntity.getStatus() == Status.ACTIVE) {
            throw new BadRequestException("Plan is already active.");
        }
        
        planEntity.setStatus(Status.ACTIVE);
        PlanEntity updatedPlan = planRepository.save(planEntity);
        
        log.info("Plan activated successfully with ID: {}", updatedPlan.getId());
        
        return planMapper.toDto(updatedPlan);
    }
}