package com.comandante.pushpackage.jackson.model;

import com.google.common.collect.ImmutableList;
import com.comandante.pushpackage.jackson.WebsiteObjectMapper;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class TestWebsite {

    private static final ObjectMapper MAPPER = WebsiteObjectMapper.getInstance();

    @Test
    public void testSerializeWebsite(){

        String webServiceUrl = "http://webserviceurl/";
        String websiteName = "website name";
        String authenticationToken = UUID.randomUUID().toString();
        String urlFormatString = "http://urlformatstring/";
        List<String> allowedDomains = ImmutableList.of(
                "http://domain1",
                "http://domain2",
                "http://domain3",
                "http://domain4",
                "http://domain5");
        String websitePushId = "com.website.push.id";

        Website website = new WebsiteBuilder()
                .setWebServiceUrl(webServiceUrl)
                .setWebsiteName(websiteName)
                .setAuthenticationToken(authenticationToken)
                .setUrlFormatString(urlFormatString)
                .setAllowedDomains(allowedDomains)
                .setWebsitePushId(websitePushId)
                .build();

        try {
            String json = MAPPER.writeValueAsString(website);
            Website website1 = MAPPER.readValue(json, Website.class);
            assertEquals(website1.getAllowedDomains().get(0), website.getAllowedDomains().get(0));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
