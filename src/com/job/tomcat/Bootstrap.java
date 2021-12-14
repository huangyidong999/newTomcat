package com.job.tomcat;

import com.job.catalina.*;


public class Bootstrap {

    public static void main(String[] args) {
        Server server  = new Server();
        server.start();
    }
}