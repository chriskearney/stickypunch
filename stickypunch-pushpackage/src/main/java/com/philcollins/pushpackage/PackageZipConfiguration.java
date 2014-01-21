package com.philcollins.pushpackage;

import com.google.common.collect.ImmutableList;
import org.apache.commons.configuration.Configuration;

import java.util.List;

public class PackageZipConfiguration {

    public static final String PUSH_PACKAGE_FILES = "stickypunch.pushpackagefiles";
    public static final String PUSH_PACKAGE_FILES_DEFAULT = "pushpackage.raw";
    public final String pushPackageFiles;

    public static final String PUSH_PACKAGE_SIGNER_CERT_PROP= "stickypunch.signercert";
    public static final String PUSH_PACKAGE_SIGNER_CERT_DEFAULT = "pushpackage.raw/cert.p12";
    public final String pushPackageSignerCertPath;

    public static final String PUSH_PACKAGE_SIGNER_CERT_PASSWORD_PROP= "stickypunch.signercertpassword";
    public static final String PUSH_PACKAGE_SIGNER_CERT_PASSWORD_DEFAULT = "password";
    public final String pushPackageSignerCertPassword;

    public static final String PUSH_PACKAGE_SIGNER_CERT_NAME_PROP= "stickypunch.signercertname";
    public static final String PUSH_PACKAGE_SIGNER_CERT_NAME_DEFAULT = "name";
    public final String pushPackageSignerCertName;

    public static final String PUSH_PACKAGE_WEBSITE_NAME_PROP = "stickypunch.websiteName";
    public static final String PUSH_PACKAGE_WEBSITE_NAME_DEFAULT = "Sticky Punch";
    public final String websiteName;

    public static final String PUSH_PACKAGE_WEBSITE_PUSH_ID = "stickypunch.websitePushID";
    public static final String PUSH_PACKAGE_WEBSITE_PUSH_ID_DEFAULT = "web.com.purplestickypunch.demo";
    public final String websitePushID;

    public static final String PUSH_PACKAGE_WEBSITE_ALLOWED_DOMAINS_PROP = "stickypunch.allowedDomains";
    public static final List<Object> PUSH_PACKAGE_WEBSITE_ALLOWED_DOMAINS_DEFAULT = ImmutableList.<Object>of("https://purplestickypunch.com");
    public final List<Object> allowedDomains;

    public static final String PUSH_PACKAGE_WEBSITE_URL_FORMAT_STRING_PROP = "stickypunch.urlFormatString";
    public static final String PUSH_PACKAGE_WEBSITE_URL_FORMAT_STRING_DEFAULT = "https://purplestickypunch.com/notificationreceived";
    public final String urlFormatString;

    public static final String PUSH_PACKAGE_WEBSITE_WEBSERVICE_URL_PROP = "stickypunch.webServiceUrl";
    public static final String PUSH_PACKAGE_WEBSITE_WEBSERVICE_URL_DEFAULT = "https://purplestickypunch.com/push";
    public final String webServiceUrl;

    public static final String PUSH_PACKAGE_PACKAGE_ZIP_QUEUE_SIZE_PROP = "stickypunch.packageZipQueueSize";
    public static final int PUSH_PACKAGE_PACKAGE_ZIP_QUEUE_SIZE_DEFAULT = 50;
    public final int packageZipQueueSize;


    public PackageZipConfiguration(Configuration configuration) {
        pushPackageFiles = configuration.getString(PUSH_PACKAGE_FILES, PUSH_PACKAGE_FILES_DEFAULT);
        pushPackageSignerCertPath = configuration.getString(PUSH_PACKAGE_SIGNER_CERT_PROP, PUSH_PACKAGE_SIGNER_CERT_DEFAULT);
        pushPackageSignerCertPassword = configuration.getString(PUSH_PACKAGE_SIGNER_CERT_PASSWORD_PROP, PUSH_PACKAGE_SIGNER_CERT_PASSWORD_DEFAULT);
        websiteName = configuration.getString(PUSH_PACKAGE_WEBSITE_NAME_PROP, PUSH_PACKAGE_WEBSITE_NAME_DEFAULT);
        websitePushID = configuration.getString(PUSH_PACKAGE_WEBSITE_PUSH_ID, PUSH_PACKAGE_WEBSITE_PUSH_ID_DEFAULT);
        allowedDomains = configuration.getList(PUSH_PACKAGE_WEBSITE_ALLOWED_DOMAINS_PROP, PUSH_PACKAGE_WEBSITE_ALLOWED_DOMAINS_DEFAULT);
        urlFormatString = configuration.getString(PUSH_PACKAGE_WEBSITE_URL_FORMAT_STRING_PROP, PUSH_PACKAGE_WEBSITE_URL_FORMAT_STRING_DEFAULT);
        webServiceUrl = configuration.getString(PUSH_PACKAGE_WEBSITE_WEBSERVICE_URL_PROP, PUSH_PACKAGE_WEBSITE_WEBSERVICE_URL_DEFAULT);
        packageZipQueueSize = configuration.getInt(PUSH_PACKAGE_PACKAGE_ZIP_QUEUE_SIZE_PROP, PUSH_PACKAGE_PACKAGE_ZIP_QUEUE_SIZE_DEFAULT);
        pushPackageSignerCertName = configuration.getString(PUSH_PACKAGE_SIGNER_CERT_NAME_PROP, PUSH_PACKAGE_SIGNER_CERT_NAME_DEFAULT);
    }
}
