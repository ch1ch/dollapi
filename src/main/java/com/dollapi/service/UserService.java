package com.dollapi.service;

import com.dollapi.domain.Invitation;
import com.dollapi.domain.UserAdress;
import com.dollapi.domain.UserInfo;
import com.dollapi.domain.UserThird;
import com.dollapi.exception.DollException;
import com.dollapi.mapper.InvitationMapper;
import com.dollapi.mapper.UserAdressMapper;
import com.dollapi.mapper.UserInfoMapper;
import com.dollapi.mapper.UserThirdMapper;
import com.dollapi.util.ApiContents;
import com.dollapi.util.ImageUploadTools;
import com.dollapi.vo.WXUserInfo;
import com.wilddog.client.*;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service("userService")
public class UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserThirdMapper userThirdMapper;

    @Autowired
    private UserAdressMapper userAdressMapper;

    private static final String USER_HEAD_IMAGE_PATH = "data/head/";

    @Value("${com.doll.appid}")
    private String appid;

    @Value("${com.doll.secret}")
    private String secret;

    @Autowired
    private InvitationMapper invitationMapper;

//    public UserService() {
//        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://wd2620361786fgzrcs.wilddogio.com").build();
//        WilddogApp.initializeApp(options);
//        SyncReference ref = WilddogSync.getInstance().getReference();
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue() != null) {
//                    logger.info("onDataChange", dataSnapshot.toString());
//
//                }
//            }
//
//            @Override
//            public void onCancelled(SyncError syncError) {
//                if (syncError != null) {
//                    logger.info("onCancelled", syncError.toString());
//                }
//            }
//        });
//
//        logger.info("okokokokokokok");
//
//    }

    public UserInfo WXLogin(String code) {

        WXUserInfo wxUserInfo = WXApi.getUserInfo(code, appid, secret);
//        WXUserInfo wxUserInfo = new WXUserInfo();
//        wxUserInfo.setUnionId("fanghexu123");
        if (wxUserInfo.getUnionId() == null || wxUserInfo.getUnionId().equals("")) {
            throw new DollException(ApiContents.WX_USER_NULL.value(), ApiContents.WX_USER_NULL.desc());
        }

        UserInfo userInfo = userInfoMapper.selectByUnionId(wxUserInfo.getUnionId());
        if (userInfo == null) {
            //新建用户
            userInfo = new UserInfo();
            userInfo.setNickName(wxUserInfo.getNickName());
            userInfo.setDollCount(0L);
            userInfo.setUserPoint(0L);
            userInfo.setUserLevel(1);
            // FIXME: 2017/10/2 100注册活动
            userInfo.setGameMoney(100L);
            userInfo.setHeadUrl(getWXImage(wxUserInfo.getHeadImgUrl(), UUID.randomUUID().toString().replaceAll("-", ""), ""));
            userInfo.setInvitationCode(UUID.randomUUID().toString().replaceAll("-", ""));
            userInfoMapper.save(userInfo);


            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            uuid.replaceAll("o", "");
            uuid.replaceAll("i", "");
            uuid.replaceAll("l", "");
            uuid.replaceAll("0", "");
            uuid = uuid.toUpperCase();
            if (userInfo.getId() < 10) {
                uuid = uuid.substring(0, 6);
                uuid = userInfo.getId().toString() + uuid;
            } else if (userInfo.getId() > 9 && userInfo.getId() < 100) {
                uuid = uuid.substring(0, 5);
                uuid = userInfo.getId().toString() + uuid;
            } else if (userInfo.getId() > 99 && userInfo.getId() < 1000) {
                uuid = uuid.substring(0, 4);
                uuid = userInfo.getId().toString() + uuid;
            } else if (userInfo.getId() > 999 && userInfo.getId() < 10000) {
                uuid = uuid.substring(0, 3);
                uuid = userInfo.getId().toString() + uuid;
            } else if (userInfo.getId() > 9999 && userInfo.getId() < 100000) {
                uuid = uuid.substring(0, 2);
                uuid = userInfo.getId().toString() + uuid;
            } else if (userInfo.getId() > 99999 && userInfo.getId() < 1000000) {
                uuid = uuid.substring(0, 1);
                uuid = userInfo.getId().toString() + uuid;
            } else {
                uuid = uuid.substring(3, 10);
            }
            userInfo.setInvitationCode(uuid);
            userInfoMapper.update(userInfo);

            UserThird userThird = new UserThird();
            userThird.setUserId(userInfo.getId());
            // FIXME: 2017/9/9 这里使用枚举
            userThird.setThirdType(1);
            userThird.setUnionId(wxUserInfo.getUnionId());
            userThirdMapper.save(userThird);
        }
        getToken(userInfo);

        return userInfo;
    }

    public void addAddress(UserAdress userAdress) {
        userAdressMapper.save(userAdress);
    }

    public List<UserAdress> getUserAddressList(Long userId) {
        List<UserAdress> list = userAdressMapper.selectByUserId(userId);
        return list;
    }


    private void getToken(UserInfo userInfo) {
        if (userInfo.getToken() == null || userInfo.getToken().equals("")) {
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            userInfo.setToken(uuid);
            userInfoMapper.update(userInfo);
        }

    }

    public String getWXImage(String urlString, String phone, String fileName) {
        InputStream is = null;
        String headingUrl = null;
        try {
            URL url = new URL(urlString);
            URLConnection con = url.openConnection();
            con.setConnectTimeout(10 * 1000);
            is = con.getInputStream();
            byte[] tmp = new byte[1024];
            int length;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();


            while ((length = is.read(tmp)) != -1) {
                baos.write(tmp, 0, length);
            }

            headingUrl = ImageUploadTools.uploadQrFile(phone + ".jpg", new ByteArrayInputStream(baos.toByteArray()), USER_HEAD_IMAGE_PATH);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return headingUrl;
    }


    public void invitation(UserInfo userInfo, String code) {
        List<Invitation> ll = invitationMapper.selectByUserId(userInfo.getId());
        if (ll != null && ll.size() > 0) {
            throw new DollException(ApiContents.Invitation_ERROR.value(), ApiContents.Invitation_ERROR.desc());
        }
        UserInfo userInfo1 = userInfoMapper.selectByCode(code);
        if (userInfo1 == null) {
            throw new DollException(ApiContents.Invitation_CODE_ERROR.value(), ApiContents.Invitation_CODE_ERROR.desc());
        }
        ll = null;
        ll = invitationMapper.selectByRecommendUserId(userInfo1.getId());
        if (ll != null && ll.size() > 19) {
            throw new DollException(ApiContents.Invitation_ERROR.value(), ApiContents.Invitation_ERROR.desc());
        }

        Invitation invitation = new Invitation();
        invitation.setUserId(userInfo.getId());
        invitation.setRecommendUserId(userInfo1.getId());
        invitation.setGameMony(70L);

        userInfo.setGameMoney(userInfo.getGameMoney() + 70L);
        userInfo1.setGameMoney(userInfo1.getGameMoney() + 70L);

        userInfoMapper.update(userInfo);
        userInfoMapper.update(userInfo1);

        invitationMapper.save(invitation);
    }

}
