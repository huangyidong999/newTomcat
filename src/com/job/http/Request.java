package com.job.http;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import com.job.catalina.Context;
import com.job.catalina.Engine;
import com.job.catalina.Host;
import com.job.catalina.Service;
import com.job.tomcat.Bootstrap;
import com.job.util.MiniBrowser;
import cn.hutool.core.util.StrUtil;

public class Request {

    private String requestString;
    private String uri;
    private Socket socket;
    private Context context;
    private Service service;
    public Request(Socket socket, Service service) throws IOException {
        this.socket = socket;
        this.service = service;
        parseHttpRequest();
        if(StrUtil.isEmpty(requestString))
            return;
        parseUri();
        parseContext();
        if(!"/".equals(context.getPath()))
            uri = StrUtil.removePrefix(uri, context.getPath());

    }



    private void parseHttpRequest() throws IOException {
        InputStream is = this.socket.getInputStream();
        byte[] bytes = MiniBrowser.readBytes(is);
        requestString = new String(bytes, "utf-8");
    }

    private void parseUri() {
        String temp;

        temp = StrUtil.subBetween(requestString, " ", " ");
        if (!StrUtil.contains(temp, '?')) {
            uri = temp;
            return;
        }
        temp = StrUtil.subBefore(temp, '?', false);
        uri = temp;
    }

    public Context getContext() {
        return context;
    }

    public String getUri() {
        return uri;
    }

    public String getRequestString(){
        return requestString;
    }

    private void parseContext() {
        String path = StrUtil.subBetween(uri, "/", "/");
        if (null == path)
            path = "/";
        else
            path = "/" + path;

        Engine engine = service.getEngine();

        context = engine.getDefaultHost().getContext(path);
        if (null == context)
            context = engine.getDefaultHost().getContext("/");
    }

}