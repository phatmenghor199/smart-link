package com.menghor.smart_shop.feature.setting.service.impl;

import com.menghor.smart_shop.enumations.Status;
import com.menghor.smart_shop.enumations.SubscriptionActionType;
import com.menghor.smart_shop.exceptoins.error.BadRequestException;
import com.menghor.smart_shop.exceptoins.error.NotFoundException;
import com.menghor.smart_shop.feature.auth.models.UserEntity;
import com.menghor.smart_shop.feature.auth.repository.UserRepository;
import com.menghor.smart_shop.feature.setting.dto.request.*;
import com.menghor.smart_shop.feature.setting.dto.resposne.SubscriptionHistoryResponseDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.SubscriptionResponseDto;
import com.menghor.smart_shop.feature.setting.mapper.SubscriptionHistoryMapper;
import com.menghor.smart_shop.feature.setting.mapper.SubscriptionMapper;
import com.menghor.smart_shop.feature.setting.model.PlanEntity;
import com.menghor.smart_shop.feature.setting.model.SubscriptionEntity;
import com.menghor.smart_shop.feature.setting.model.SubscriptionHistoryEntity;
import com.menghor.smart_shop.feature.setting.repository.PlanRepository;
import com.menghor.smart_shop.feature.setting.repository.SubscriptionHistoryRepository;
import com.menghor.smart_shop.feature.setting.repository.SubscriptionRepository;
import com.menghor.smart_shop.feature.setting.service.SubscriptionService;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionHistoryRepository historyRepository;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final SubscriptionHistoryMapper historyMapper;

    @Override
    @Transactional
    public SubscriptionResponseDto createSubscription(SubscriptionRequestDto subscriptionRequestDto) {
        log.info("Creating subscription for user ID: {} with plan ID: {}",
                subscriptionRequestDto.getUserId(), subscriptionRequestDto.getPlanId());

        // Check if user already has an active subscription
        Optional<SubscriptionEntity> existingSubscription = subscriptionRepository.findActiveSubscriptionForUser(
                subscriptionRequestDto.getUserId(), LocalDateTime.now());

        if (existingSubscription.isPresent()) {
            throw new BadRequestException("User already has an active subscription.");
        }

        // Get user and plan
        UserEntity user = userRepository.findById(subscriptionRequestDto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + subscriptionRequestDto.getUserId()));

        PlanEntity plan = planRepository.findById(subscriptionRequestDto.getPlanId())
                .orElseThrow(() -> new NotFoundException("Plan not found with ID: " + subscriptionRequestDto.getPlanId()));

        if (plan.getStatus() == Status.INACTIVE) {
            throw new BadRequestException("Cannot subscribe to an inactive plan.");
        }

        // Create subscription
        SubscriptionEntity subscription = subscriptionMapper.toEntity(subscriptionRequestDto);
        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusDays(plan.getDurationDays()));
        subscription.setStatus(Status.ACTIVE);
        subscription.setAutoRenew(subscriptionRequestDto.getAutoRenew());

        SubscriptionEntity savedSubscription = subscriptionRepository.save(subscription);

        // Create subscription history
        createSubscriptionHistory(savedSubscription, SubscriptionActionType.CREATED, null);

        log.info("Subscription created successfully with ID: {}", savedSubscription.getId());

        return subscriptionMapper.toDto(savedSubscription);
    }

    @Override
    public SubscriptionResponseDto getSubscriptionById(Long subscriptionId) {
        log.info("Getting subscription by ID: {}", subscriptionId);

        SubscriptionEntity subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Subscription not found with ID: " + subscriptionId));

        return subscriptionMapper.toDto(subscription);
    }

    @Override
    public SubscriptionResponseDto getActiveSubscriptionForUser(Long userId) {
        log.info("Getting active subscription for user ID: {}", userId);

        SubscriptionEntity subscription = subscriptionRepository.findActiveSubscriptionForUser(userId, LocalDateTime.now())
                .orElseThrow(() -> new NotFoundException("No active subscription found for user ID: " + userId));

        return subscriptionMapper.toDto(subscription);
    }

    @Override
    public CustomPaginationResponseDto<SubscriptionHistoryResponseDto> getSubscriptionHistoryByUserId(

            SubscriptionHistoryFilterDto filterDto
    ) {
        log.info("Getting subscription history for user ID: {} with filter: {}", filterDto.getUserId(), filterDto);

        // Validate user exists
        userRepository.findById(filterDto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + filterDto.getUserId()));

        // Fetch subscription history
        List<SubscriptionHistoryEntity> allHistory = historyRepository.findByUserId(filterDto.getUserId());

        // Apply status filter if provided
        List<SubscriptionHistoryEntity> filteredHistory = allHistory.stream()
                .filter(history -> filterDto.getStatus() == null || history.getStatus() == filterDto.getStatus())
                .filter(history -> filterDto.getActionType() == null || history.getActionType() == filterDto.getActionType())
                .sorted(Comparator.comparing(SubscriptionHistoryEntity::getCreatedAt).reversed())
                .collect(Collectors.toList());

        // Apply pagination
        int start = (filterDto.getPageNo() - 1) * filterDto.getPageSize();
        int end = Math.min(start + filterDto.getPageSize(), filteredHistory.size());
        List<SubscriptionHistoryEntity> pagedHistory = filteredHistory.subList(start, end);

        // Convert to DTOs
        List<SubscriptionHistoryResponseDto> historyDtos = pagedHistory.stream()
                .map(historyMapper::toDto)
                .collect(Collectors.toList());

        // Create pagination response
        CustomPaginationResponseDto<SubscriptionHistoryResponseDto> response = new CustomPaginationResponseDto<>();
        response.setContent(historyDtos);
        response.setPageNo(filterDto.getPageNo());
        response.setPageSize(filterDto.getPageSize());
        response.setTotalElements(filteredHistory.size());
        response.setTotalPages((int) Math.ceil((double) filteredHistory.size() / filterDto.getPageSize()));
        response.setLast(filterDto.getPageNo() >= response.getTotalPages());

        return response;
    }

    @Override
    @Transactional
    public SubscriptionResponseDto updateSubscription(SubscriptionUpdateDto updateDto) {
        log.info("Updating active subscription for user ID: {}", updateDto.getUserId());

        // Verify user exists
        UserEntity user = userRepository.findById(updateDto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + updateDto.getUserId()));

        // Find the active subscription for the user
        SubscriptionEntity subscription = subscriptionRepository.findActiveSubscriptionForUser(
                        updateDto.getUserId(), LocalDateTime.now())
                .orElseThrow(() -> new NotFoundException("No active subscription found for user ID: " + updateDto.getUserId()));

        // Update only the fields that are provided
        if (updateDto.getTransactionId() != null) {
            subscription.setTransactionId(updateDto.getTransactionId());
        }

        if (updateDto.getAutoRenew() != null) {
            subscription.setAutoRenew(updateDto.getAutoRenew());
        }

        if (updateDto.getAmountPaid() != null) {
            subscription.setAmountPaid(updateDto.getAmountPaid());
        }

        SubscriptionEntity savedSubscription = subscriptionRepository.save(subscription);

        log.info("Subscription updated successfully with ID: {}", savedSubscription.getId());

        return subscriptionMapper.toDto(savedSubscription);
    }

    @Override
    @Transactional
    public SubscriptionResponseDto renewSubscription(SubscriptionRenewalDto renewalDto) {
        log.info("Renewing subscription for user ID: {}", renewalDto.getUserId());

        // Find the user's active subscription
        SubscriptionEntity subscription = subscriptionRepository.findActiveSubscriptionForUser(renewalDto.getUserId(), LocalDateTime.now())
                .orElseThrow(() -> new NotFoundException("No active subscription found for user ID: " + renewalDto.getUserId()));

        // Check subscription status
        if (subscription.getStatus() != Status.ACTIVE && subscription.getStatus() != Status.EXPIRED) {
            throw new BadRequestException("Cannot renew a subscription that is not active or expired.");
        }

        PlanEntity plan = subscription.getPlan();

        // If previous subscription is still active, extend the current one instead of creating a new one
        if (subscription.getStatus() == Status.ACTIVE && subscription.getEndDate().isAfter(LocalDateTime.now())) {
            subscription.setEndDate(subscription.getEndDate().plusDays(plan.getDurationDays()));
            subscription.setTransactionId(renewalDto.getTransactionId());
            subscription.setAmountPaid(renewalDto.getAmountPaid());

            SubscriptionEntity savedSubscription = subscriptionRepository.save(subscription);

            // Create subscription history
            createSubscriptionHistory(savedSubscription, SubscriptionActionType.RENEWED, renewalDto.getNotes());

            log.info("Subscription extended successfully with ID: {}", savedSubscription.getId());

            return subscriptionMapper.toDto(savedSubscription);
        }

        // Create a new subscription as a renewal
        SubscriptionEntity renewedSubscription = new SubscriptionEntity();
        renewedSubscription.setUser(subscription.getUser());
        renewedSubscription.setPlan(plan);
        renewedSubscription.setStartDate(LocalDateTime.now());
        renewedSubscription.setEndDate(LocalDateTime.now().plusDays(plan.getDurationDays()));
        renewedSubscription.setStatus(Status.ACTIVE);
        renewedSubscription.setAutoRenew(subscription.getAutoRenew());
        renewedSubscription.setTransactionId(renewalDto.getTransactionId());
        renewedSubscription.setAmountPaid(renewalDto.getAmountPaid());
        renewedSubscription.setPreviousSubscriptionId(subscription.getId());

        SubscriptionEntity savedSubscription = subscriptionRepository.save(renewedSubscription);

        // Update the status of the old subscription
        subscription.setStatus(Status.INACTIVE);
        subscriptionRepository.save(subscription);

        // Create subscription history
        createSubscriptionHistory(savedSubscription, SubscriptionActionType.RENEWED, renewalDto.getNotes());

        log.info("Subscription renewed successfully with ID: {}", savedSubscription.getId());

        return subscriptionMapper.toDto(savedSubscription);
    }

    @Override
    @Transactional
    public SubscriptionResponseDto changeSubscriptionPlan(SubscriptionPlanChangeDto planChangeDto) {
        log.info("Changing plan for user ID: {} to plan ID: {}", planChangeDto.getUserId(), planChangeDto.getNewPlanId());

        // Find the user's active subscription
        SubscriptionEntity subscription = subscriptionRepository.findActiveSubscriptionForUser(planChangeDto.getUserId(), LocalDateTime.now())
                .orElseThrow(() -> new NotFoundException("No active subscription found for user ID: " + planChangeDto.getUserId()));

        if (subscription.getStatus() != Status.ACTIVE) {
            throw new BadRequestException("Cannot change plan for a subscription that is not active.");
        }

        PlanEntity newPlan = planRepository.findById(planChangeDto.getNewPlanId())
                .orElseThrow(() -> new NotFoundException("Plan not found with ID: " + planChangeDto.getNewPlanId()));

        if (newPlan.getStatus() != Status.ACTIVE) {
            throw new BadRequestException("Cannot change to an inactive plan.");
        }

        // Determine action type based on plan price
        SubscriptionActionType actionType = newPlan.getPrice() > subscription.getPlan().getPrice()
                ? SubscriptionActionType.UPGRADED
                : SubscriptionActionType.DOWNGRADED;

        // Create a new subscription with the new plan
        SubscriptionEntity newSubscription = new SubscriptionEntity();
        newSubscription.setUser(subscription.getUser());
        newSubscription.setPlan(newPlan);
        newSubscription.setStartDate(LocalDateTime.now());
        newSubscription.setEndDate(LocalDateTime.now().plusDays(newPlan.getDurationDays()));
        newSubscription.setStatus(Status.ACTIVE);
        newSubscription.setAutoRenew(subscription.getAutoRenew());
        newSubscription.setTransactionId(planChangeDto.getTransactionId());
        newSubscription.setAmountPaid(planChangeDto.getAmountPaid());
        newSubscription.setPreviousSubscriptionId(subscription.getId());

        SubscriptionEntity savedSubscription = subscriptionRepository.save(newSubscription);

        // Update the status of the old subscription
        subscription.setStatus(Status.INACTIVE);
        subscriptionRepository.save(subscription);

        // Create subscription history
        createSubscriptionHistory(savedSubscription, actionType,
                planChangeDto.getNotes() != null ? planChangeDto.getNotes() :
                        "Changed from " + subscription.getPlan().getName() + " to " + newPlan.getName());

        log.info("Plan changed successfully for user. New subscription ID: {}", savedSubscription.getId());

        return subscriptionMapper.toDto(savedSubscription);
    }

    @Override
    @Transactional
    public SubscriptionResponseDto cancelSubscription(Long userId, String reason) {
        log.info("Canceling subscription for user ID: {}", userId);

        // Find the user's active subscription
        SubscriptionEntity subscription = subscriptionRepository.findActiveSubscriptionForUser(userId, LocalDateTime.now())
                .orElseThrow(() -> new NotFoundException("No active subscription found for user ID: " + userId));

        if (subscription.getStatus() != Status.ACTIVE) {
            throw new BadRequestException("Cannot cancel a subscription that is not active.");
        }

        subscription.setStatus(Status.CANCELED);
        SubscriptionEntity savedSubscription = subscriptionRepository.save(subscription);

        // Create subscription history
        createSubscriptionHistory(savedSubscription, SubscriptionActionType.CANCELED, reason);

        log.info("Subscription canceled successfully with ID: {}", savedSubscription.getId());

        return subscriptionMapper.toDto(savedSubscription);
    }

    @Override
    @Transactional
    public void processExpiredSubscriptions() {
        log.info("Processing expired subscriptions");

        List<SubscriptionEntity> expiredSubscriptions = subscriptionRepository.findExpiredSubscriptions(LocalDateTime.now());

        for (SubscriptionEntity subscription : expiredSubscriptions) {
            log.info("Processing expired subscription with ID: {}", subscription.getId());

            subscription.setStatus(Status.EXPIRED);
            SubscriptionEntity savedSubscription = subscriptionRepository.save(subscription);

            // Create subscription history
            createSubscriptionHistory(savedSubscription, SubscriptionActionType.EXPIRED, "Subscription has expired");

            // Handle auto-renewal if applicable
            if (subscription.getAutoRenew()) {
                log.info("Auto-renewing subscription with ID: {}", subscription.getId());

                try {
                    // Prepare auto-renewal DTO
                    SubscriptionRenewalDto renewalDto = new SubscriptionRenewalDto();
                    renewalDto.setUserId(subscription.getUser().getId());
                    renewalDto.setTransactionId("AUTO-RENEWAL-" + System.currentTimeMillis());
                    renewalDto.setAmountPaid(subscription.getPlan().getPrice());
                    renewalDto.setNotes("Auto-renewal");

                    // Renew subscription
                    renewSubscription(renewalDto);
                } catch (Exception e) {
                    log.error("Failed to auto-renew subscription with ID: {}", subscription.getId(), e);
                }
            }
        }

        log.info("Processed {} expired subscriptions", expiredSubscriptions.size());
    }

    /**
     * Helper method to create subscription history record
     */
    private void createSubscriptionHistory(SubscriptionEntity subscription, SubscriptionActionType actionType, String notes) {
        SubscriptionHistoryEntity history = new SubscriptionHistoryEntity();
        history.setUser(subscription.getUser());
        history.setPlan(subscription.getPlan());
        history.setStartDate(subscription.getStartDate());
        history.setEndDate(subscription.getEndDate());
        history.setStatus(subscription.getStatus());
        history.setActionType(actionType);
        history.setSubscriptionId(subscription.getId());
        history.setTransactionId(subscription.getTransactionId());
        history.setAmountPaid(subscription.getAmountPaid());
        history.setNotes(notes);

        historyRepository.save(history);

        log.info("Created subscription history for subscription ID: {} with action: {}",
                subscription.getId(), actionType);
    }
}