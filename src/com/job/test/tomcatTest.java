package com.job.test;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.job.util.MiniBrowser;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.job.util.MiniBrowser.getContentBytes;

public class tomcatTest {
    private static int port = 18080;
    private static String ip = "127.0.0.1";
    @BeforeClass
    public static void beforeClass() {
        //所有测试开始前看diy tomcat 是否已经启动了
        if(NetUtil.isUsableLocalPort(port)) {
            System.err.println("请先启动 位于端口: " +port+ " 的diy tomcat，否则无法进行单元测试");
            System.exit(1);
        }
        else {
            System.out.println("检测到 diy tomcat已经启动，开始进行单元测试");
        }
    }

    @Test
    public void testHelloTomcat() {
        String html = getContentString("/");
        Assert.assertEquals(html,"Hello DIY Tomcat from how2j.cn");
    }

    @Test
    public void testTimeConsumeHtml() throws InterruptedException {
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 20, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(10));
        TimeInterval timeInterval = DateUtil.timer();

        for(int i = 0; i<3; i++){
            threadPool.execute(new Runnable(){
                public void run() {
                    getContentString("/timeConsume.html");
                }
            });
        }
        threadPool.shutdown();
        threadPool.awaitTermination(1, TimeUnit.HOURS);

        long duration = timeInterval.intervalMs();

        Assert.assertTrue(duration < 3000);
    }

    @Test
    public void testaIndex() {
        String html = getContentString("/a");
        Assert.assertEquals(html,"Hello I hope I will get a job");
    }

    @Test
    public void testbIndex() {
        String html = getContentString("/b/");
        Assert.assertEquals(html,"Hello I hope I will get a job");
    }

    @Test
    public void test404() {
        String response  = getHttpString("/not_exist.html");
        containAssert(response, "HTTP/1.1 404 Not Found");
    }
    @Test
    public void test500() {
        String response  = getHttpString("/500.html");
        containAssert(response, "HTTP/1.1 500 Internal Server Error");
    }

    @Test
    public void testaTxt() {
        String response  = getHttpString("/a.txt");
        containAssert(response, "Content-Type: text/plain");
    }

    @Test
    public void testPNG() {
        byte[] bytes = getContentBytes("/logo.png");
        int pngFileLength = 1672;
        Assert.assertEquals(pngFileLength, bytes.length);
    }
    @Test
    public void testPDF() {
        byte[] bytes = getContentBytes("/etf.pdf");
        int pngFileLength = 3590775;
        Assert.assertEquals(pngFileLength, bytes.length);
    }

    @Test
    public void testhello() {
        String html = getContentString("/j2ee/hello");
        Assert.assertEquals(html,"Hello DIY Tomcat from HelloServlet");
    }

    @Test
    public void testJavawebHello() {
        String html = getContentString("/javaweb/hello");
        containAssert(html,"Hello DIY Tomcat from HelloServlet@javaweb");
    }

    @Test
    public void testJavawebHelloSingleton() {
        String html1 = getContentString("/javaweb/hello");
        String html2 = getContentString("/javaweb/hello");
        Assert.assertEquals(html1,html2);
    }
    @Test
    public void testgetParam() {
        String uri = "/j2ee/param";
        String url = StrUtil.format("http://{}:{}{}", ip,port,uri);
        Map<String,Object> params = new HashMap<>();
        params.put("name","meepo");
        String html = MiniBrowser.getContentString(url, params, true);
        Assert.assertEquals(html,"get name:meepo");
    }
    @Test
    public void testpostParam() {
        String uri = "/j2ee/param";
        String url = StrUtil.format("http://{}:{}{}", ip,port,uri);
        Map<String,Object> params = new HashMap<>();
        params.put("name","meepo");
        String html = MiniBrowser.getContentString(url, params, false);
        Assert.assertEquals(html,"post name:meepo");

    }
    private byte[] getContentBytes(String uri) {
        return getContentBytes(uri,false);
    }
    private byte[] getContentBytes(String uri,boolean gzip) {
        String url = StrUtil.format("http://{}:{}{}", ip,port,uri);
        return MiniBrowser.getContentBytes(url,false);
    }
    private String getContentString(String uri) {
        String url = StrUtil.format("http://{}:{}{}", ip,port,uri);
        String content = MiniBrowser.getContentString(url);
        return content;
    }
    private String getHttpString(String uri) {
        String url = StrUtil.format("http://{}:{}{}", ip,port,uri);
        String http = MiniBrowser.getHttpString(url);
        return http;
    }

    @Test
    public void testheader() {
        String html = getContentString("/j2ee/header");
        Assert.assertEquals(html,"how2j mini brower / java1.8");
    }

    @Test
    public void testgetCookie() throws IOException {
        String url = StrUtil.format("http://{}:{}{}", ip,port,"/j2ee/getCookie");
        URL u = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setRequestProperty("Cookie","name=Gareen(cookie)");
        conn.connect();
        InputStream is = conn.getInputStream();
        String html = IoUtil.read(is, "utf-8");
        containAssert(html,"name:Gareen(cookie)");
    }

    private void containAssert(String html, String string) {
        boolean match = StrUtil.containsAny(html, string);
        Assert.assertTrue(match);
    }
}
