package com.comandante.stickypunch.http;

import com.google.common.base.Optional;
import com.comandante.stickypunch.api.model.WebPushStore;
import com.comandante.stickypunch.api.model.WebPushStoreAuth;
import com.comandante.stickypunch.api.model.WebPushUser;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class WebPushUserIdAuth implements WebPushStoreAuth {

    private static final Logger log = LogManager.getLogger(WebPushUserIdAuth.class);

    private final WebPushStore webPushStore;
    private final HttpConfiguration httpConfiguration;

    public WebPushUserIdAuth(WebPushStore webPushStore, HttpConfiguration configuration) {
        this.webPushStore = webPushStore;
        this.httpConfiguration = configuration;
    }

    @Override
    public boolean authUserId(String userId, Optional<String> deviceToken) {
        if (!httpConfiguration.isStrictAuth) {
            return true;
        }
        Optional<WebPushUser> webPushUser = Optional.absent();
        if (deviceToken.isPresent()) {
            webPushUser = webPushStore.getWebPushUsersByUserIdAndDeviceToken(userId, deviceToken.get());
        } else {
            webPushUser = webPushStore.getWebPushUsersByUserId(userId);
        }
        if (webPushUser.isPresent() && webPushUser.get().getUserId().isPresent()) {
            if (webPushUser.get().getUserId().get().equals(userId)) {
                return true;
            }
        }
        return false;
    }
}
