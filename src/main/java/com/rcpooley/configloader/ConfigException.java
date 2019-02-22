package com.rcpooley.configloader;

public class ConfigException extends Exception {

    private static String buildMessage(String message, Object[] tags) {
        StringBuilder msg = new StringBuilder(message);
        for (int i = 0; i < tags.length; i+= 2) {
            String tag = tags[i].toString();
            String val = i + 1 < tags.length ? tags[i + 1].toString() : "[NO VALUE PROVIDED]";
            msg.append("\n\t").append(tag).append(" = ").append(val);
        }
        return msg.toString();
    }

    public ConfigException(String message, Throwable cause, Object... tags) {
        super(buildMessage(message, tags), cause);
    }

    public ConfigException(Throwable e) {
        super(e);
    }

    public ConfigException(String msg, Object... tags) {
        this(msg, null, tags);
    }

}
