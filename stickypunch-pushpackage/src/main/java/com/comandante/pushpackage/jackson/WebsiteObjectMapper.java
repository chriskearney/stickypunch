package com.comandante.pushpackage.jackson;

import com.comandante.pushpackage.jackson.model.Website;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

public class WebsiteObjectMapper {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        SimpleModule httpModule = new SimpleModule("HttpModule", new Version(1, 0, 0, null))
                .addDeserializer(Website.class, WebsiteDeserializer.INSTANCE)
                .addSerializer(Website.class, WebsiteSerializer.INSTANCE);

        MAPPER.registerModule(httpModule);

    }

    public static ObjectMapper getInstance() {
        return MAPPER;
    }

    private WebsiteObjectMapper() {
    }

}

