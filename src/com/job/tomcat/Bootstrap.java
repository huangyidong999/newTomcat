package com.job.tomcat;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.job.http.Request;
import com.job.http.Response;
import com.job.util.Constant;

import java.io.File;
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
                Response response=new Response();

                String uri = request.getRequestUri();
                if(null==uri){
                    continue;
                }
                if("/".equals(uri)){
                    String html = "Hello I hope I will get a job";
                    response.getPrintWriter().println(html);
                }
                else{
                    String fileName = StrUtil.removePrefix(uri,"/");
                    File file = FileUtil.file(Constant.rootFolder,fileName);
                    if(file.exists()){
                        String fileContent = FileUtil.readUtf8String(file);
                        response.getPrintWriter().println(fileContent);
                    }
                    else {
                        response.getPrintWriter().println("File Not Found");
                    }
                }
                System.out.println("浏览器的输入信息:"+request.getRequestString());
                System.out.println("浏览器的URI:"+request.getRequestUri());

                handler200(s, response);
                s.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void handler200(Socket socket, Response response) throws IOException {
        //得到响应内容
        String contentType=response.getContentType();
        //得到响应消息头
        String header200= Constant.RESPONSE_HEAD_202;
        //连接成完整的http响应
        header200= StrUtil.format(header200, contentType);
        byte[] content=response.getBody();
        byte[] header=header200.getBytes();
        //转换成字节,拼接字节http响应
        byte[] headerContent=new byte[content.length+header.length];
        ArrayUtil.copy(header, 0, headerContent,0,header.length);
        ArrayUtil.copy(content, 0, headerContent, header.length,content.length );
        OutputStream outputStream=socket.getOutputStream();
        outputStream.write(headerContent);
        socket.close();
    }
}
