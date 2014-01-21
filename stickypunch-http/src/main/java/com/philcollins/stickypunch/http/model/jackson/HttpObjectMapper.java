package com.philcollins.stickypunch.http.model.jackson;

import com.philcollins.stickypunch.api.model.WebPushUser;
import com.philcollins.stickypunch.http.resource.jackson.LogDeserializer;
import com.philcollins.stickypunch.http.resource.jackson.WebPushUserSerializer;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

public class HttpObjectMapper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        SimpleModule httpModule = new SimpleModule("HttpModule", new Version(1, 0, 0, null))
                .addDeserializer(WebPushLog.class, LogDeserializer.INSTANCE)
                .addSerializer(WebPushUser.class, WebPushUserSerializer.INSTANCE);

        MAPPER.registerModule(httpModule);

    }

    public static ObjectMapper getInstance() {
        return MAPPER;
    }

    private HttpObjectMapper() {
    }

}

