package com.comandante.stickypunch.http.resource.jackson;

import com.comandante.stickypunch.http.model.jackson.WebPushLog;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;

public class LogDeserializer extends JsonDeserializer<WebPushLog> {

    public static final LogDeserializer INSTANCE = new LogDeserializer();

    @Override
    public WebPushLog deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectCodec oc = jsonParser.getCodec();
        ArrayList<String> logMsgs = new ArrayList<String>();
        for (JsonNode node: oc.readTree(jsonParser).get("logs")) {
            logMsgs.add(node.getTextValue());
        }

        return new WebPushLog(logMsgs);
    }
}
