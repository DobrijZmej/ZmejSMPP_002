package org.dobrijzmej.smpp;

public class MessageQueue {
    private String phone;
    private String message;
    private String alias;

    public MessageQueue(String phone, String message, String alias) {
        this.phone = phone;
        this.message = message;
        this.alias = alias;
    }

    public String getPhone() {
        return phone;
    }

    public String getMessage() {
        return message;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public String toString() {
        return "Message on alias ["+alias+"] for phone ["+phone+"]: [" + message + ']';
    }
}
