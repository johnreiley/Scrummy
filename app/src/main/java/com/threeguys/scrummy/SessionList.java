package com.threeguys.scrummy;

import java.util.ArrayList;
import java.util.List;

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
}
