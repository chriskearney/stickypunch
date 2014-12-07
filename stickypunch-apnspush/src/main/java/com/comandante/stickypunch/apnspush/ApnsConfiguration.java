package com.comandante.stickypunch.apnspush;

import org.apache.commons.configuration.Configuration;

public class ApnsConfiguration {

    public static final String APNS_CERT_LOCATION_PROP = "stickypunch.apnscert";
    public static final String APNS_CERT_LOCATION_DEFAULT = "apnscert_path_here";
    public final String apnsCertLocation;

    public static final String APNS_CERT_PASSWORD_PROP = "stickypunch.apnscertpassword";
    public static final String APNS_CERT_PASSWORD_DEFAULT = "pass";
    public final String apnsCertPassword;

    public static final String APNS_FEEDBACK_POLL_INTERVAL_SECONDS_PROP = "stickypunch.apnsfeedbackpollinterval";
    public static final int APNS_FEEDBACK_POLL_INTERVAL_SECONDS_DEFAULT = 60;
    public final int apnsFeedbackPollInterval;

    public ApnsConfiguration(Configuration configuration) {
        this.apnsCertLocation = configuration.getString(APNS_CERT_LOCATION_PROP, APNS_CERT_LOCATION_DEFAULT);
        this.apnsCertPassword = configuration.getString(APNS_CERT_PASSWORD_PROP, APNS_CERT_PASSWORD_DEFAULT);
        this.apnsFeedbackPollInterval = configuration.getInt(APNS_FEEDBACK_POLL_INTERVAL_SECONDS_PROP, APNS_FEEDBACK_POLL_INTERVAL_SECONDS_DEFAULT);
    }
}
