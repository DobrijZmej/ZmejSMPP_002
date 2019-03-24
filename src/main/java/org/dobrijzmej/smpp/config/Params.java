package org.dobrijzmej.smpp.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Params {
    public Map<String, Object> general;
    public Map<String, User> users;
    public Map<String, Output> outputs;

    public Params() {
        general = new LinkedHashMap<>();
        users = new LinkedHashMap<>();
        outputs = new LinkedHashMap<>();
    }

    public Map<String, Object> getGeneral() {
        return general;
    }

    public void setGeneral(Map<String, Object> general) {
        this.general = general;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public void setUsers(Map<String, User> users) {
        this.users = users;
    }

    public Map<String, Output> getOutputs() {
        return outputs;
    }

    public void setOutputs(Map<String, Output> outputs) {
        this.outputs = outputs;
    }
}


