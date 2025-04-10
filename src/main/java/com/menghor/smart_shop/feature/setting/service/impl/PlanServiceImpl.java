package com.menghor.smart_shop.feature.setting.service.impl;

import com.menghor.smart_shop.enumations.Status;
import com.menghor.smart_shop.exceptions.error.BadRequestException;
import com.menghor.smart_shop.exceptions.error.NotFoundException;
import com.menghor.smart_shop.feature.setting.dto.request.PlanRequestDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.PlanResponseDto;
import com.menghor.smart_shop.feature.setting.mapper.PlanMapper;
import com.menghor.smart_shop.feature.setting.model.PlanEntity;
import com.menghor.smart_shop.feature.setting.repository.PlanRepository;
import com.menghor.smart_shop.feature.setting.service.PlanService;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;
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
    public CustomPaginationResponseDto<PlanResponseDto> getFilteredPlans(String name, Status status, int pageNo, int pageSize) {
        log.info("Getting plans with filters - name: {}, status: {}, page: {}, size: {}",
                name, status, pageNo, pageSize);

        List<PlanEntity> filteredPlans;

        // Determine if we need to filter by status
        if (status != null) {
            filteredPlans = planRepository.findByStatus(status);
            log.info("Found {} plans with status: {}", filteredPlans.size(), status);
        } else {
            filteredPlans = planRepository.findAll();
            log.info("Found {} total plans", filteredPlans.size());
        }

        // Apply name filter if provided
        if (name != null && !name.trim().isEmpty()) {
            String nameLowerCase = name.toLowerCase();
            filteredPlans = filteredPlans.stream()
                    .filter(plan -> plan.getName().toLowerCase().contains(nameLowerCase))
                    .collect(Collectors.toList());
            log.info("After name filtering, found {} plans", filteredPlans.size());
        }

        // Sort plans by price (ascending)
        filteredPlans = filteredPlans.stream()
                .sorted((p1, p2) -> {
                    if (p1.getPrice() == null) return -1;
                    if (p2.getPrice() == null) return 1;
                    return p1.getPrice().compareTo(p2.getPrice());
                })
                .collect(Collectors.toList());

        // Apply pagination
        int totalElements = filteredPlans.size();
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        // Adjust pageNo to be 0-based for calculation
        int startIndex = (pageNo - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalElements);

        // Guard against invalid indices
        if (startIndex >= totalElements) {
            startIndex = 0;
            endIndex = Math.min(pageSize, totalElements);
            pageNo = 1; // Reset to first page if out of bounds
        }

        List<PlanEntity> pagedPlans = filteredPlans.subList(startIndex, endIndex);

        // Convert to DTOs
        List<PlanResponseDto> planDtos = pagedPlans.stream()
                .map(planMapper::toDto)
                .collect(Collectors.toList());

        // Create CustomPaginationResponseDto
        CustomPaginationResponseDto<PlanResponseDto> response = new CustomPaginationResponseDto<>();
        response.setContent(planDtos);
        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);
        response.setLast(pageNo >= totalPages);

        return response;
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
    public PlanResponseDto updatePlanStatus(Long planId, Status status) {
        log.info("Updating status of plan with ID: {} to {}", planId, status);

        if (status == null) {
            throw new BadRequestException("Status cannot be null");
        }

        PlanEntity planEntity = planRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan not found with ID: " + planId));

        if (planEntity.getStatus() == status) {
            throw new BadRequestException("Plan is already " + status.name().toLowerCase() + ".");
        }

        planEntity.setStatus(status);
        PlanEntity updatedPlan = planRepository.save(planEntity);

        log.info("Plan status updated successfully to {} for ID: {}", status, updatedPlan.getId());

        return planMapper.toDto(updatedPlan);
    }
}