package com.comandante.stickypunch.api.model;

import com.google.common.base.Optional;

import java.util.List;

public class WebPushStoreDummy implements WebPushStore {
    @Override
    public Optional<WebPushUser> getWebPushUserMostRecentlyUpdatedByDeviceToken(String deviceToken) {
        return null;
    }

    @Override
    public Optional<WebPushUser> getWebPushUsersByUserId(String userId) {
        return null;
    }

    @Override
    public Optional<WebPushUser> getWebPushUsersByUserIdAndDeviceToken(String userId, String deviceToken) {
        return null;
    }

    @Override
    public void updateWebPushUser(WebPushUser webPushUser) throws Exception {
    }

    @Override
    public Optional<List<WebPushUser>> getWebPushUsers() {
        return null;
    }
}
