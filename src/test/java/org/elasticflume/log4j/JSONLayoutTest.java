package org.elasticflume.log4j;

import junit.framework.TestCase;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.spi.LoggingEvent;

public class JSONLayoutTest extends TestCase {

    public void testEvent() {
        MDC.put("UserId", "123456");
        MDC.put("ProjectId", "98765");

        LoggingEvent loggingEvent = new LoggingEvent("", Logger.getLogger("org.elasticsearch"), Level.INFO, "Hello World", new Exception());

        JSONLayout jsonLayout = new JSONLayout();
        jsonLayout.setMdcsToUse("UserId,ProjectId");
        jsonLayout.activateOptions();


        String jsonFormatted = jsonLayout.format(loggingEvent);

        System.out.println(jsonFormatted);

    }
}
