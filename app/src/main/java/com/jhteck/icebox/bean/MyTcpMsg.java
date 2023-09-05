package com.jhteck.icebox.bean;

/**
 * @author wade
 * @Description:(用来装TCP消息)
 * @date 2023/6/30 20:44
 */
public class MyTcpMsg {

    private String type;
    private String content;

    public MyTcpMsg(String type, String content) {
        this.type = type;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
