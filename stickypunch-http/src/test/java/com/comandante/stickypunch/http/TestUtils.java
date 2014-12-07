package com.comandante.stickypunch.http;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.ServerSocket;

public class TestUtils {

    public static int findFreePort() {
        try {
            ServerSocket server = new ServerSocket(0);
            int port = server.getLocalPort();
            server.close();
            return port;
        } catch (IOException ie) {
            return 1025 + (int) (Math.random() * 32767);
        }
    }

    public static String getRandomLogJson() {
        JsonFactory jsonfactory = new JsonFactory();
        Writer writer = new StringWriter();
        String json = null;
        try {
            JsonGenerator jsonGenerator = jsonfactory.createJsonGenerator(writer);
            jsonGenerator.writeStartObject();
            jsonGenerator.writeArrayFieldStart("logs");
            jsonGenerator.writeString("authenticationToken must be at least 16 characters.");
            jsonGenerator.writeString("Downloading push notification package failed.");
            jsonGenerator.writeString("Extracting push notification package failed.");
            jsonGenerator.writeString("Missing file in push notification package.");
            jsonGenerator.writeString("Missing image in push notification package.");
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
            jsonGenerator.close();
            json = writer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }
}
