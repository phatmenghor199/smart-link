package com.menghor.smart_shop.config;

import com.menghor.smart_shop.enumations.RoleEnum;
import com.menghor.smart_shop.feature.auth.models.Role;
import com.menghor.smart_shop.feature.auth.repository.RoleRepository;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class DefaultRoleInitializer {

    private final RoleRepository roleRepository;

    public DefaultRoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void initRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(RoleEnum.ADMIN));
            roleRepository.save(new Role(RoleEnum.MANAGER));
            roleRepository.save(new Role(RoleEnum.DEVELOPER));
            roleRepository.save(new Role(RoleEnum.SHOP_ADMIN));
            roleRepository.save(new Role(RoleEnum.SHOP_STAFF));
        }
    }
}
