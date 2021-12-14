package com.job.catalina;

import com.job.util.ServerXMLUtil;

public class Service {
    private String name;
    private Engine engine;
    private Server server;
    public Service(Server server){
        this.server = server;
        this.name = ServerXMLUtil.getServiceName();
        this.engine = new Engine(this);
    }

    public Engine getEngine() {
        return engine;
    }

    public Server getServer() {
        return server;
    }
}
