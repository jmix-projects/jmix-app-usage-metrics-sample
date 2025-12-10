package com.company.businessmetrics.app;

import com.company.businessmetrics.entity.LoginEvent;
import com.company.businessmetrics.entity.User;
import io.jmix.core.UnconstrainedDataManager;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class UserLoginListener {
    private final UnconstrainedDataManager unconstrainedDataManager;

    public UserLoginListener(UnconstrainedDataManager unconstrainedDataManager) {
        this.unconstrainedDataManager = unconstrainedDataManager;
    }

    @EventListener
    public void onInteractiveAuthenticationSuccess(InteractiveAuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        String username = authentication.getName();

        LoginEvent loginEvent = unconstrainedDataManager.create(LoginEvent.class);
        loginEvent.setUserName(username);
        User user = unconstrainedDataManager
                .load(User.class)
                .query("select u from User u where u.username = :userName")
                .parameter("userName", username)
                .one();
        loginEvent.setUserId(user.getId());
        loginEvent.setLoginTime(OffsetDateTime.now());

        unconstrainedDataManager.save(loginEvent);
    }
}