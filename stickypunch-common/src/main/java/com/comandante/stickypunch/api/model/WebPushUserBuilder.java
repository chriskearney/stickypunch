package com.comandante.stickypunch.api.model;

import com.google.common.base.Optional;

public class WebPushUserBuilder {
    private Optional<String> deviceToken = Optional.absent();
    private Optional<String> websitePushId = Optional.absent();
    private Optional<String> userId = Optional.absent();
    private Optional<Boolean> active = Optional.absent();
    private Optional<Long> isActiveTimestamp = Optional.absent();

    public WebPushUserBuilder setDeviceToken(String deviceToken) {
        this.deviceToken = Optional.fromNullable(deviceToken);
        return this;
    }

    public WebPushUserBuilder setWebsitePushId(String websitePushId) {
        this.websitePushId = Optional.fromNullable(websitePushId);
        return this;
    }

    public WebPushUserBuilder setUserId(String userId) {
        this.userId = Optional.of(userId);
        return this;
    }

    public WebPushUserBuilder setActive(Boolean active) {
        this.active = Optional.of(active);
        return this;
    }

    public WebPushUserBuilder setIsActiveTimestamp(Long timestamp) {
        this.isActiveTimestamp = Optional.of(timestamp);
        return this;
    }

    public WebPushUser build() {
        return new WebPushUser(deviceToken, websitePushId, userId, active, isActiveTimestamp);
    }
}