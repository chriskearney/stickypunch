package com.comandante.pushpackage.jackson.model;

import java.util.List;

public class WebsiteBuilder {
    private String websiteName;
    private String websitePushId;
    private List<String> allowedDomains;
    private String urlFormatString;
    private String authenticationToken;
    private String webServiceUrl;

    public WebsiteBuilder setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
        return this;
    }

    public WebsiteBuilder setWebsitePushId(String websitePushId) {
        this.websitePushId = websitePushId;
        return this;
    }

    public WebsiteBuilder setAllowedDomains(List<String> allowedDomains) {
        this.allowedDomains = allowedDomains;
        return this;
    }

    public WebsiteBuilder setUrlFormatString(String urlFormatString) {
        this.urlFormatString = urlFormatString;
        return this;
    }

    public WebsiteBuilder setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
        return this;
    }

    public WebsiteBuilder setWebServiceUrl(String webServiceUrl) {
        this.webServiceUrl = webServiceUrl;
        return this;
    }

    public Website build() {
        return new Website(websiteName, websitePushId, allowedDomains, urlFormatString, authenticationToken, webServiceUrl);
    }
}