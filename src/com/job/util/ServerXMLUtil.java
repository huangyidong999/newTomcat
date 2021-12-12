package com.job.util;

import cn.hutool.core.io.FileUtil;
import com.job.catalina.Engine;
import com.job.catalina.Host;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import com.job.catalina.Context;

public class ServerXMLUtil {
    public static List<Context> getContexts() {
        List<Context> result = new ArrayList<>();
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        //here we use jsoup to read the xml files
        Document d = Jsoup.parse(xml);

        Elements es = d.select("Context");
        for (Element e : es) {
            String path = e.attr("path");
            String docBase = e.attr("docBase");
            Context context = new Context(path, docBase);
            result.add(context);
        }
        return result;
    }

    public static String getServiceName() {
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);
        Element host = d.select("Service").first();
        return host.attr("name");
    }


    public static String getEngineDefaultHost() {
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);
        Element host = d.select("Engine").first();
        return host.attr("defaultHost");
    }

    public static List<Host> getHosts(Engine engine) {
        List<Host> result = new ArrayList<>();
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);
        Elements es = d.select("Host");
        for (Element e : es) {
            String name = e.attr("name");
            Host host = new Host(name,engine);
            result.add(host);
        }
        return result;
    }
}
