package org.elasticflume.log4j;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

import java.io.IOException;
import java.io.StringWriter;

public class JSONLayout extends Layout {

    private String mdcsToUse = "";
    private String[] mdcKeystoUse = new String[0];

    private final JsonFactory jsonFactory = new JsonFactory();

    public String getMdcsToUse() {
        return mdcsToUse;
    }

    public void setMdcsToUse(String mdcsToUse) {
        this.mdcsToUse = mdcsToUse;
    }

    @Override
    public String format(LoggingEvent event) {
        try {
            StringWriter stringWriter = new StringWriter();
            JsonGenerator g = jsonFactory.createJsonGenerator(stringWriter);
            g.writeStartObject();
            g.writeStringField("logger", event.getLoggerName());
            g.writeStringField("level", event.getLevel().toString());
            g.writeNumberField("timestamp", event.timeStamp);
            g.writeStringField("threadName", event.getThreadName());
            g.writeStringField("message", event.getMessage().toString());
            String throwableString = null;
            if (mdcKeystoUse.length > 0) {
                event.getMDCCopy();

                g.writeObjectFieldStart("MDC");
                for (String s : mdcKeystoUse) {
                    Object mdc = event.getMDC(s);
                    if (mdc != null) {
                        g.writeStringField(s, mdc.toString());
                    }
                }
                g.writeEndObject();

            }
            String[] throwableStrRep = event.getThrowableStrRep();
            throwableString = "";
            for (String s : throwableStrRep) {
                throwableString += s + "\n";
            }

            if (event.getNDC() != null) {
                g.writeStringField("NDC", event.getNDC());
            }

            if (throwableString.length() > 0) {
                g.writeStringField("throwable", throwableString);
            }
            g.writeEndObject();

            g.close();

            return stringWriter.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean ignoresThrowable() {
        return false;
    }

    @Override
    public void activateOptions() {
        mdcKeystoUse = mdcsToUse.split(",");
    }

}
