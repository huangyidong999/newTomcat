package com.job.catalina;

import com.job.util.ServerXMLUtil;

public class Service {
    private String name;
    private Engine engine;
    public Service(){
        this.name = ServerXMLUtil.getServiceName();
        this.engine = new Engine(this);
    }

    public Engine getEngine() {
        return engine;
    }
}
