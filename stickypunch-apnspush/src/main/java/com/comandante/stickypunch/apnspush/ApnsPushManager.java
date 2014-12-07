package com.comandante.stickypunch.apnspush;

import com.google.common.base.Optional;
import com.notnoop.apns.ApnsService;
import com.comandante.stickypunch.api.model.WebPushStore;
import com.comandante.stickypunch.api.model.WebPushUser;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ApnsPushManager {
    final ApnsService apnsService;
    final ApnsConfiguration apnsConfiguration;
    final WebPushStore webPushStore;

    private static final Logger log = LogManager.getLogger(ApnsPushManager.class);

    public ApnsPushManager(ApnsService apnsService, ApnsConfiguration apnsConfiguration, WebPushStore webPushStore) {
        this.apnsService = apnsService;
        this.apnsConfiguration = apnsConfiguration;
        this.webPushStore = webPushStore;
    }

    public boolean sendPush(String deviceToken, String title, String body) {
        Optional<WebPushUser> webPushUserOptional = webPushStore.getWebPushUserMostRecentlyUpdatedByDeviceToken(deviceToken);
        if (!webPushUserOptional.isPresent()) {
            return false;
        }
        if (!webPushUserOptional.get().getActive().get()) {
            return false;
        }
        String payload = String.format("{\"aps\":{\"alert\":{\"title\":\"%s\",\"body\":\"%s\",\"action\":\"View\"},\"url-args\":[]}}", title, body);
        apnsService.push(deviceToken, payload);
        log.info("Sent simple push for " + deviceToken + ", msg: " + body);
        return true;
    }
}
