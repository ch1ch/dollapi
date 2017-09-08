package com.dollapi.servie;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSON;
import com.dollapi.vo.WXUserInfo;
import org.apache.commons.collections.map.HashedMap;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * <p>Copyright: All Rights Reserved</p>
 * <p>Company: 指点无限(北京)科技有限公司   http://www.zhidianwuxian.cn</p>
 * <p>Description:  </p>
 * <p>Author:hexu/方和煦, 2017/9/8</p>
 */
public class WXApi {

    private static final String BASE_URL = "https://api.weixin.qq.com/sns/";

    private static Logger logger = LoggerFactory.getLogger(WXApi.class);

    public static WXUserInfo getUserInfo(String code, String appid, String secret) {
        WXUserInfo user = new WXUserInfo();
        String responseText = null;
        String url = BASE_URL + "oauth2/access_token?appid=" + appid + "&secret=" + secret + "&code=" + code + "&grant_type=authorization_code";
        Map<String, Object> map = new HashedMap();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httppost = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseText = EntityUtils.toString(entity, "UTF-8");
                logger.info("getUserInfo微信返回:"+responseText);
                JSONObject object = JSON.parseObject(responseText);
                user.setAccessToken(object.get("access_token") == null ? null : object.get("access_token").toString());
                user.setOpenId(object.get("openid") == null ? null : object.get("openid").toString());
                user.setUnionId(object.get("unionid") == null ? null : object.get("unionid").toString());
            }


            if (user.getUnionId() != null) {
                CloseableHttpClient httpclient2 = HttpClients.createDefault();
                url = BASE_URL + "userinfo?access_token=" + user.getAccessToken() + "&openid=" + user.getOpenId();
                HttpGet httppost2 = new HttpGet(url);
                CloseableHttpResponse response2 = null;
                response2 = httpclient2.execute(httppost2);
                HttpEntity entity2 = response2.getEntity();
                if (entity2 != null) {
                    String txt = EntityUtils.toString(entity2, "UTF-8");
                    JSONObject object = JSON.parseObject(txt);
                    user.setNickName(object.get("nickname") == null ? null : object.get("nickname").toString());
                    user.setHeadImgUrl(object.get("headimgurl") == null ? null : object.get("headimgurl").toString());
                }
                httpclient2.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    public static WXUserInfo getUserInfo(String accessToken, String openId) {
        WXUserInfo user = new WXUserInfo();
        String responseText = null;
        String url = BASE_URL + "userinfo?access_token=" + accessToken + "&openid=" + openId;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httppost = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseText = EntityUtils.toString(entity, "UTF-8");
                System.out.println(responseText);
                JSONObject object = JSON.parseObject(responseText);
                user.setNickName(object.get("nickname") == null ? null : object.get("nickname").toString());
                user.setHeadImgUrl(object.get("headimgurl") == null ? null : object.get("headimgurl").toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

}
