package com.soecode.lyf.web.citic.bean;

/**
 * Created by gaodao on 2017/10/19.
 */
public class CiticToken {

    private String access_token;
    private String refresh_token; //刷新token时使用
    private String uid;
    private String expires_in;//有效时间，单位为秒

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }
}
