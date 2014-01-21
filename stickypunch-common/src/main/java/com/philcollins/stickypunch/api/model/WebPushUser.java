package com.philcollins.stickypunch.api.model;

import com.google.common.base.Optional;

public class WebPushUser {
    private Optional<String> deviceToken;
    private Optional<String> websitePushId;
    private Optional<String> userId;
    private Optional<Boolean> isActive;
    private Optional<Long> isActiveTimestamp;

    public WebPushUser(Optional<String> deviceToken,
                       Optional<String> websitePushId,
                       Optional<String> userId,
                       Optional<Boolean> active,
                       Optional<Long> isActiveTimestamp) {
        this.deviceToken = deviceToken;
        this.websitePushId = websitePushId;
        this.userId = userId;
        this.isActive = active;
        this.isActiveTimestamp = isActiveTimestamp;
    }

    public Optional<String> getDeviceToken() {
        return deviceToken;
    }

    public Optional<String> getWebsitePushId() {
        return websitePushId;
    }

    public Optional<String> getUserId() {
        return userId;
    }

    public Optional<Boolean> getActive() {
        return isActive;
    }

    public Optional<Long> getActiveTimestamp() {
        return isActiveTimestamp;
    }

    public void setActiveTimestamp(Long activeTimestamp) {
        isActiveTimestamp = Optional.of(activeTimestamp);
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = Optional.of(deviceToken);
    }

    public void setWebsitePushId(String websitePushId) {
        this.websitePushId = Optional.of(websitePushId);
    }

    public void setUserId(String userId) {
        this.userId = Optional.of(userId);
    }

    public void setActive(Boolean active) {
        isActive = Optional.of(active);
    }
}
