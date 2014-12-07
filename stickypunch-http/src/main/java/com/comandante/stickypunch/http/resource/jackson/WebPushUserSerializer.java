package com.comandante.stickypunch.http.resource.jackson;

import com.comandante.stickypunch.api.model.WebPushUser;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WebPushUserSerializer extends JsonSerializer<WebPushUser> {

    public static final WebPushUserSerializer INSTANCE = new WebPushUserSerializer();

    @Override
    public void serialize(WebPushUser value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        if (value.getDeviceToken().isPresent()) {
            jgen.writeStringField("deviceToken", value.getDeviceToken().get());
        }
        if (value.getActive().isPresent()) {
            jgen.writeBooleanField("isActive", value.getActive().get());
        }
        if (value.getUserId().isPresent()) {
            jgen.writeStringField("userId", value.getUserId().get());
        }
        if (value.getWebsitePushId().isPresent()) {
            jgen.writeStringField("websitePushId", value.getWebsitePushId().get());
        }
        if (value.getActiveTimestamp().isPresent()) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
            Date dt = new Date(value.getActiveTimestamp().get());
            String readableDate = sdf.format(dt);
            jgen.writeStringField("activeDate", readableDate);
        }
        jgen.writeEndObject();
    }
}
