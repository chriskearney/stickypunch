package com.philcollins.stickypunch.api.model;

import com.google.common.base.Optional;

import java.util.List;

public interface WebPushStore {

    void updateWebPushUser(WebPushUser webPushUser) throws Exception;

    Optional<WebPushUser> getWebPushUserMostRecentlyUpdatedByDeviceToken(String deviceToken);

    Optional<WebPushUser> getWebPushUsersByUserId(String userId);

    Optional<WebPushUser> getWebPushUsersByUserIdAndDeviceToken(String userId, String deviceToken);

    Optional<List<WebPushUser>> getWebPushUsers();

}
