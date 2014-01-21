package com.philcollins.stickypunch.apnspush;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.notnoop.apns.ApnsService;
import com.philcollins.stickypunch.api.model.WebPushStore;
import com.philcollins.stickypunch.api.model.WebPushUser;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ApnsFeedbackService extends AbstractScheduledService {

    private static final Logger log = LogManager.getLogger(ApnsFeedbackService.class);

    private final ApnsService apnsService;
    private final ApnsConfiguration apnsConfiguration;
    private final WebPushStore webPushStore;

    public ApnsFeedbackService(ApnsService apnsService, ApnsConfiguration apnsConfiguration, WebPushStore webPushStore) {
        this.apnsService = apnsService;
        this.apnsConfiguration = apnsConfiguration;
        this.webPushStore = webPushStore;
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, apnsConfiguration.apnsFeedbackPollInterval, TimeUnit.SECONDS);

    }

    @Override
    protected void runOneIteration() throws Exception {
        Map<String,Date> inactiveDevices = apnsService.getInactiveDevices();
        for (String deviceToken : inactiveDevices.keySet()) {
            Date inactiveAsOf = inactiveDevices.get(deviceToken);
            Optional<WebPushUser> userOptional = getUserIfValidTime(deviceToken, inactiveAsOf);
            if (userOptional.isPresent()) {
                userOptional.get().setActive(false);
                userOptional.get().setActiveTimestamp(System.currentTimeMillis());
                log.info("APNS FEEDBACK: Marking a user inActive.");
                webPushStore.updateWebPushUser(userOptional.get());
            }
        }
    }

    private Optional<WebPushUser> getUserIfValidTime(String deviceToken, Date inActiveAsOf) throws SQLException {
        Optional<WebPushUser> webPushUser = webPushStore.getWebPushUserMostRecentlyUpdatedByDeviceToken(deviceToken);
        if (webPushUser.isPresent()) {
            WebPushUser currentUser = webPushUser.get();
            if (inActiveAsOf.getTime() > currentUser.getActiveTimestamp().get()) {
                return Optional.of(currentUser);
            }
        }
        return Optional.absent();
    }

}
