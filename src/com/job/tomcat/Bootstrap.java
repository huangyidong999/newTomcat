package com.job.tomcat;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.system.SystemUtil;
import com.job.http.Request;
import com.job.http.Response;
import com.job.util.Constant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Bootstrap {
    public static void main(String[] args) {

        try {
            logJVM();
            int port = 18080;

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
            LogFactory.get().error(e);
            e.printStackTrace();
        }

    }

    private static void logJVM(){
        Map<String,String> infos = new LinkedHashMap<>();
        infos.put("Server Version","MyTomcat/1.0.1");
        infos.put("Server built","2020-04-08 10:20:22");
        infos.put("Server number","1.0.1");
        infos.put("OS Name\t", SystemUtil.get("os.name"));
        infos.put("OS Version", SystemUtil.get("os.version"));
        infos.put("Architecture",SystemUtil.get("os.arch"));
        infos.put("Java Home",SystemUtil.get("java.runtime.version"));
        infos.put("JVM Vendor",SystemUtil.get("Java.vm.specification.vendor"));

        Set<String> keys = infos.keySet();
        for(String key : keys){
            LogFactory.get().info(key +"\t\t" + infos.get(key));
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
