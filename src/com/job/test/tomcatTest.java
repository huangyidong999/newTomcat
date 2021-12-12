package com.job.test;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.job.util.MiniBrowser;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class tomcatTest {
    private static int port = 18080;
    private static String ip = "127.0.0.1";

    /**
     * 使用Junit的@BeforeClass检测diyTomcat是否已经启动
     **/
    @BeforeClass
    public static void beforeClass() {
        if (NetUtil.isUsableLocalPort(port)) {
            System.err.println("18080端口没有打开，请先打开diyTomcat");
            System.exit(1);
        } else {
            System.out.println("diyTomcat已经启动");
        }
    }

    /**
     * 使用Junit的@Test注解进行测试
     **/
    @Test
    public void testDiyTomcat() {
        /*请求返回String类型的内容字符串*/
        String html = getContentString("/");
        /*利用Junit的Assert进行返回内容的对比*/
        Assert.assertEquals(html, "Hello I hope I will get a job");
    }

    //test
    @Test
    public  void  testaHtml(){
        String html = getContentString("/a.html");
        Assert.assertEquals(html,"Hello I hope I will get a job");
    }

    @Test
    public void testIndex() {
        String html = getContentString("/a/index.html");
        Assert.assertEquals(html,"Hello I hope I will get a job");
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
    /**
     * 仿造浏览器请求
     **/
    public String getContentString(String uri) {
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
        return MiniBrowser.getContentString(url);
    }
}
