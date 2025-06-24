package io.github.java_zengguang.litepress.core.log;

import org.tinylog.configuration.Configuration;
import org.tinylog.format.AdvancedMessageFormatter;

import java.util.Locale;

public class TraceIdMessageFormatter extends AdvancedMessageFormatter {

    public TraceIdMessageFormatter(Locale locale, boolean escape) {
        super(locale, escape);
    }

    public TraceIdMessageFormatter() {
        super(Configuration.getLocale(), Configuration.isEscapingEnabled());
    }

    @Override
    public String format(String s, Object[] objects) {
        return "[" + TraceIdHolder.getTraceId() + "] " + super.format(s, objects);
    }
}