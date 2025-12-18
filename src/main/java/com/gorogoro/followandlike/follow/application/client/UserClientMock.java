package com.gorogoro.followandlike.follow.application.client;

import org.springframework.stereotype.Component;

@Component
public class UserClientMock implements UserClient {
    @Override
    public boolean exists(Long userId) {
        return true;
    }
}
