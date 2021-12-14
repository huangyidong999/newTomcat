package com.job.http;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class Response {
    private StringWriter stringWriter;
    private PrintWriter printWriter;
    private String contentType;
    private byte[] body;
    public Response(){
        this.contentType="text/html";
        this.stringWriter=new StringWriter();
        this.printWriter=new PrintWriter(stringWriter);
    }

    public PrintWriter getWriter() {
        return printWriter;
    }

    public String getContentType() {
        return contentType;
    }


    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public byte[] getBody() throws UnsupportedEncodingException {
        if(null==body) {
            String content = stringWriter.toString();
            body = content.getBytes("utf-8");
        }
        return body;
    }
}
