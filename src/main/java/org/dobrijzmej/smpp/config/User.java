package org.dobrijzmej.smpp.config;

public class User {
    private String username;
    private String password;
    private String alias;

    public User(String username, String password, String alias) {
        this.username = username;
        this.password = password;
        this.alias = alias;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public User() {
    }
}
