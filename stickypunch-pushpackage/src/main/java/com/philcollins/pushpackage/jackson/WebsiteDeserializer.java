package com.philcollins.pushpackage.jackson;

import com.philcollins.pushpackage.jackson.model.Website;
import com.philcollins.pushpackage.jackson.model.WebsiteBuilder;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebsiteDeserializer extends JsonDeserializer<Website> {

    public static final WebsiteDeserializer INSTANCE = new WebsiteDeserializer();

    @Override
    public Website deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec oc = jp.getCodec();
        JsonNode nodes = oc.readTree(jp);
        String websitePushID = nodes.get("websitePushID").getTextValue();
        String websiteName = nodes.get("websiteName").getTextValue();
        List<String> allowedDomains = new ArrayList<String>();
        for (JsonNode node : nodes.get("allowedDomains")) {
            allowedDomains.add(node.getTextValue());
        }
        String urlFormatString = nodes.get("urlFormatString").getTextValue();
        String authenticationToken = nodes.get("authenticationToken").getTextValue();
        String webServiceUrl = nodes.get("webServiceURL").getTextValue();

        return new WebsiteBuilder()
                .setWebsiteName(websiteName)
                .setWebsitePushId(websitePushID)
                .setAllowedDomains(allowedDomains)
                .setUrlFormatString(urlFormatString)
                .setAuthenticationToken(authenticationToken)
                .setWebServiceUrl(webServiceUrl)
                .build();
    }
}
