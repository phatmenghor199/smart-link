package com.menghor.smart_shop.feature.auth.service.impl;

import com.menghor.smart_shop.constants.ErrorMessages;
import com.menghor.smart_shop.enumations.Status;
import com.menghor.smart_shop.exceptoins.error.BadRequestException;
import com.menghor.smart_shop.exceptoins.error.NotFoundException;
import com.menghor.smart_shop.feature.auth.dto.request.ChangePasswordByAdminRequestDto;
import com.menghor.smart_shop.feature.auth.dto.request.ChangePasswordRequestDto;
import com.menghor.smart_shop.feature.auth.dto.resposne.UserDto;
import com.menghor.smart_shop.feature.auth.dto.resposne.UserResponseDto;
import com.menghor.smart_shop.feature.auth.mapper.UserMapper;
import com.menghor.smart_shop.feature.auth.models.UserEntity;
import com.menghor.smart_shop.feature.auth.repository.UserRepository;
import com.menghor.smart_shop.feature.auth.service.UserService;
import com.menghor.smart_shop.feature.setting.dto.resposne.SubscriptionResponseDto;
import com.menghor.smart_shop.feature.setting.mapper.SubscriptionMapper;
import com.menghor.smart_shop.feature.setting.model.SubscriptionEntity;
import com.menghor.smart_shop.feature.setting.repository.SubscriptionRepository;
import com.menghor.smart_shop.utils.database.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Override
    public UserResponseDto getAllUser(int pageNo, int pageSize, String search) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<UserEntity> userPage;
        if (search != null && !search.isEmpty()) {
            log.info("User is get and search by : {}", search);
            userPage = userRepository.findByUsernameContainingIgnoreCase(search, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        // Get all user IDs to fetch subscriptions in bulk
        List<Long> userIds = userPage.getContent().stream()
                .map(UserEntity::getId)
                .collect(Collectors.toList());

        // Fetch all active subscriptions for these users in a single query
        Map<Long, SubscriptionEntity> activeSubscriptions = new HashMap<>();
        if (!userIds.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();

            // Use the batch query method to get all active subscriptions
            List<SubscriptionEntity> subscriptions = subscriptionRepository.findActiveSubscriptionsForUsers(userIds, now);

            // Create a map of userId -> subscription for quick lookup
            for (SubscriptionEntity subscription : subscriptions) {
                activeSubscriptions.put(subscription.getUser().getId(), subscription);
            }

            log.info("Found {} active subscriptions for {} users", subscriptions.size(), userIds.size());
        }

        // Map users with subscription info
        List<UserDto> content = userPage.getContent().stream()
                .map(user -> {
                    UserDto userDto = userMapper.toDto(user);

                    // Ensure hasActiveSubscription is never null (setting default)
                    if (userDto.getHasActiveSubscription() == null) {
                        userDto.setHasActiveSubscription(false);
                    }

                    // Use the subscription from our map if it exists
                    SubscriptionEntity subscription = activeSubscriptions.get(user.getId());
                    if (subscription != null) {
                        SubscriptionResponseDto subscriptionDto = subscriptionMapper.toDto(subscription);
                        userDto.setActiveSubscription(subscriptionDto);
                        userDto.setHasActiveSubscription(true);
                    } else {
                        // Explicitly set to false if no subscription found
                        userDto.setActiveSubscription(null);
                        userDto.setHasActiveSubscription(false);
                    }

                    return userDto;
                })
                .collect(Collectors.toList());

        return userMapper.toPageDto(content, userPage);
    }

    @Override
    public UserDto getUserById(Long id) {
        UserEntity user = userRepository.findUserWithShopById(id)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.USER_NOT_FOUND, id)));

        UserDto userDto = userMapper.toDto(user);
        enrichUserWithSubscription(userDto, id);

        return userDto;
    }

    @Override
    public UserDto getUserByToken() {
        UserEntity currentUser = securityUtils.getCurrentUser();
        UserDto userDto = userMapper.toDto(currentUser);
        enrichUserWithSubscription(userDto, currentUser.getId());

        return userDto;
    }

    @Transactional
    @Override
    public UserDto deleteUserId(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.USER_NOT_FOUND, id)));
        user.getRoles().clear();
        userRepository.deleteById(id);
        return userMapper.toDto(user);
    }

    @Override
    public UserDto changePassword(ChangePasswordRequestDto requestDto) {
        UserEntity user = securityUtils.getCurrentUser();

        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException(ErrorMessages.CURRENT_PASSWORD_INCORRECT);
        }

        if (!requestDto.getNewPassword().equals(requestDto.getConfirmNewPassword())) {
            throw new BadRequestException(ErrorMessages.PASSWORDS_DO_NOT_MATCH);
        }

        user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
        UserEntity userEntity = userRepository.save(user);

        UserDto userDto = userMapper.toDto(userEntity);
        enrichUserWithSubscription(userDto, userEntity.getId());

        return userDto;
    }

    @Override
    public UserDto changePasswordByAdmin(ChangePasswordByAdminRequestDto requestDto) {
        UserEntity user = userRepository.findById(requestDto.getId())
                .orElseThrow(() -> {
                    log.error("User with id {} not found", requestDto.getId());
                    return new NotFoundException(String.format(ErrorMessages.USER_NOT_FOUND, requestDto.getId()));
                });

        // Optionally, verify that new password and confirm password match
        if (!requestDto.getNewPassword().equals(requestDto.getConfirmNewPassword())) {
            throw new BadRequestException(ErrorMessages.PASSWORDS_DO_NOT_MATCH);
        }

        user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
        UserEntity userEntity = userRepository.save(user);

        UserDto userDto = userMapper.toDto(userEntity);
        enrichUserWithSubscription(userDto, userEntity.getId());

        return userDto;
    }

    /**
     * Enrich user DTO with subscription information
     */
    private void enrichUserWithSubscription(UserDto userDto, Long userId) {
        // Get active subscription if exists
        Optional<SubscriptionEntity> subscription =
                subscriptionRepository.findActiveSubscriptionForUser(userId, LocalDateTime.now());

        if (subscription.isPresent()) {
            SubscriptionResponseDto subscriptionDto = subscriptionMapper.toDto(subscription.get());
            userDto.setActiveSubscription(subscriptionDto);
            userDto.setHasActiveSubscription(true);
        } else {
            userDto.setHasActiveSubscription(false);
        }
    }
}