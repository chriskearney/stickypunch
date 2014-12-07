package com.comandante.stickypunch.http.model.jackson;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class WebPushLog {
    private static final Logger log = LogManager.getLogger(WebPushLog.class);
    private final List<String> logMessages;

    public WebPushLog(List<String> logMessages) {
        this.logMessages = logMessages;
    }

    public void flush() {
        for (String logMsg: logMessages){
            log.info(logMsg);
        }
    }
}
