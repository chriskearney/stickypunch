package com.philcollins.pushpackage.jackson.model;

import java.util.List;

public class Website {
    /*{
    "websiteName": "Crunchy Punch",
    "websitePushID": "web.com.purplestickypunch.demo",
    "allowedDomains": ["https://purplestickypunch.com"]
    "urlFormatString": "https://purplestickypunch.com/notificationreceived",
    "authenticationToken": "8c7d540d-b440-4ab8-9d3a-e8f8252dea25",
    "webServiceURL": "https://purplestickypunch.com/push"
    }*/
    private String websiteName;
    private String websitePushId;
    private List<String> allowedDomains;
    private String urlFormatString;
    private String authenticationToken;
    private String webServiceUrl;

    public Website(String websiteName, String websitePushId, List<String> allowedDomains, String urlFormatString, String authenticationToken, String webServiceUrl) {
        this.websiteName = websiteName;
        this.websitePushId = websitePushId;
        this.allowedDomains = allowedDomains;
        this.urlFormatString = urlFormatString;
        this.authenticationToken = authenticationToken;
        this.webServiceUrl = webServiceUrl;
    }

    public String getWebServiceUrl() {
        return webServiceUrl;
    }

    public void setWebServiceUrl(String webServiceUrl) {
        this.webServiceUrl = webServiceUrl;
    }

    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    public String getUrlFormatString() {
        return urlFormatString;
    }

    public void setUrlFormatString(String urlFormatString) {
        this.urlFormatString = urlFormatString;
    }

    public List<String> getAllowedDomains() {
        return allowedDomains;
    }

    public void setAllowedDomains(List<String> allowedDomains) {
        this.allowedDomains = allowedDomains;
    }

    public String getWebsitePushId() {
        return websitePushId;
    }

    public void setWebsitePushId(String websitePushId) {
        this.websitePushId = websitePushId;
    }

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }
}
