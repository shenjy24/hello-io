package com.jonas.feature.broadcast;

import lombok.Data;

import java.net.InetSocketAddress;

/**
 * LogEvent
 *
 * @author shenjy
 * @version 1.0
 * @date 2022-02-20
 */
@Data
public final class LogEvent {
    public static final byte SEPARATOR = (byte) ':';

    private final InetSocketAddress source;
    private final String logfile;
    private final String msg;
    private final long received;

    public LogEvent(String logfile, String msg) {
        this(null, logfile, msg, -1);
    }

    public LogEvent(InetSocketAddress source, String logfile, String msg, long received) {
        this.source = source;
        this.logfile = logfile;
        this.msg = msg;
        this.received = received;
    }
}
