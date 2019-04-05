package com.threeguys.scrummy;

import java.util.ArrayList;
import java.util.List;

/**
 * This class simply holds a list of Sessions and is used for gson encoding and decoding.
 */
public class SessionList {

    private List<Session> list;

    public SessionList() {
        list = new ArrayList<>();
    }

    public List<Session> getList() {
        return list;
    }

    public void setList(List<Session> list) {
        this.list = list;
    }

    public void addSession(Session s) {
        list.add(s);
    }
}
