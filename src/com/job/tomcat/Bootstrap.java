package com.job.tomcat;
import cn.hutool.core.util.NetUtil;
import com.job.http.Request;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Bootstrap {
    public static void main(String[] args) {

        try {
            int port = 18080;

            if(!NetUtil.isUsableLocalPort(port)) {
                System.out.println(port +" 端口已经被占用了，排查并关闭本端口的办法请用：\r\nhttps://how2j.cn/k/tomcat/tomcat-portfix/545.html");
                return;
            }
            ServerSocket ss = new ServerSocket(port);

            while(true) {
                //这个是服务器获得套接字方法
                Socket s =  ss.accept();
                Request request=new Request(s);
                System.out.println("浏览器的输入信息:"+request.getRequestString());
                System.out.println("浏览器的URI:"+request.getRequestUri());

                OutputStream os = s.getOutputStream();
                String response_head = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n\r\n";
                String responseString = "Hello I hope I will get a job";
                responseString = response_head + responseString;
                os.write(responseString.getBytes());
                os.flush();
                s.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
