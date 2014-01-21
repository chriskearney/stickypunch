package com.philcollins.pushpackage.jackson;

import com.philcollins.pushpackage.jackson.model.Website;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;

public class WebsiteSerializer extends JsonSerializer<Website> {

    public static final WebsiteSerializer INSTANCE = new WebsiteSerializer();

    private WebsiteSerializer(){}

    @Override
    public void serialize(Website value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeStringField("websiteName", value.getWebsiteName());
        jgen.writeStringField("websitePushID", value.getWebsitePushId());
        jgen.writeArrayFieldStart("allowedDomains");
        for (String allowedDomain: value.getAllowedDomains()) {
            jgen.writeString(allowedDomain);
        }
        jgen.writeEndArray();
        jgen.writeStringField("urlFormatString", value.getUrlFormatString());
        jgen.writeStringField("authenticationToken", value.getAuthenticationToken());
        jgen.writeStringField("webServiceURL", value.getWebServiceUrl());
        jgen.writeEndObject();
    }
}
